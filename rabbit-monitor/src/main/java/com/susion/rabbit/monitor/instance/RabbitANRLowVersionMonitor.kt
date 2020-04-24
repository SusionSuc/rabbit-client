package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.FileObserver
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.greendao.RabbitAnrInfoDao
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.utils.RabbitMonitorUtils
import com.susion.rabbit.storage.RabbitStorage
import java.io.File

/**
 * susionwang at 2020-03-16
 *
 * android 21以下 通过 监控 /data/anr/trace.txt 来感知 anr 产生
 *
 * 系统每次新产生的anr信息都会覆盖文件原来的信息。
 *
 * 不会在anr上报时立即存储anr信息，会找其他恰当的时机来同步anr数据, 这样可以保证数据的完整、不丢失数据。
 */
internal class RabbitANRLowVersionMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private val calculatePeriodMS = 15000
    private var lastTime: Long = 0
    private val traceFileObserver by lazy {
        object : FileObserver("data/anr/", CLOSE_WRITE) {
            override fun onEvent(event: Int, path: String?) {
                if (path.isNullOrEmpty()) return
                RabbitLog.d(TAG_MONITOR, "monitor anr $path")
                val filepath = "/data/anr/$path"
                if (filepath.contains("trace")) {
                    RabbitAsync.asyncRun({
                        parseAnrFile(filepath)
                    })
                }
            }
        }
    }
    private val pageShowedListener = object : RabbitMonitor.PageChangeListener {
        override fun onPageShow() {
            syncAnrRecord()
        }
    }

    override fun open(context: Context) {
        try {
            traceFileObserver.startWatching()
        } catch (e: Exception) {
            RabbitLog.d(TAG_MONITOR, "anr file observer start failed")
        }
        RabbitMonitor.addPageChangeListener(pageShowedListener)
        syncAnrRecord()
        isOpen = true
    }

    override fun close() {
        traceFileObserver.stopWatching()
        RabbitMonitor.removePageChangeListener(pageShowedListener)
        isOpen = false
    }

    /**
     * 记录简要的anr信息,找时机补全 [syncAnrRecord]
     * */
    fun parseAnrFile(filepath: String) {

        val anrInfo = RabbitAnrInfo()
        val anrTime = System.currentTimeMillis()

        if (anrTime - lastTime < calculatePeriodMS) {
            return
        }

        lastTime = anrTime

        RabbitMonitorUtils.fillSimpleAnrInfo(anrInfo, File(filepath))

        RabbitLog.d(TAG_MONITOR, "采集anr信息成功 ! save to db")

        RabbitStorage.saveSync(anrInfo)

    }

    private fun syncAnrRecord() {
        RabbitLog.d(TAG_MONITOR, "syncAnrRecord---> ")
        RabbitStorage.getAll(RabbitAnrInfo::class.java,
            condition = Pair(RabbitAnrInfoDao.Properties.Invalid, false),
            orderDesc = true,
            loadResult = { anrList ->
                RabbitLog.d(TAG_MONITOR, " syncAnrRecord anr size : ${anrList.size}")
                for (anr in anrList) {
                    anr.stackStr = RabbitMonitorUtils.getAnrStack(anr)
                    if (anr.stackStr.isNotEmpty()) {
                        anr.invalid = true
                        RabbitLog.d(TAG_MONITOR, "回溯 ANR -> ${anr.time}")
                        RabbitStorage.updateOrCreate(
                            RabbitAnrInfo::class.java,
                            anr,
                            anr.id
                        )
                    } else {
                        RabbitStorage.delete(RabbitAnrInfo::class.java, anr.id)
                        RabbitLog.d(TAG_MONITOR, "删除无效的ANR记录 -> ${anr.time}")
                    }
                }
            })
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.ANR

}