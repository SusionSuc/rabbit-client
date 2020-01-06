package com.susion.rabbit.report

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.common.DeviceUtils
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitDeviceInfo
import com.susion.rabbit.base.entities.RabbitReportInfo
import com.susion.rabbit.base.config.RabbitReportConfig
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.util.concurrent.Executors

/**
 * susionwang at 2019-12-05
 * 数据上报
 */
object RabbitReport {

    private val TAG = javaClass.simpleName
    lateinit var application: Application
    var mConfig: RabbitReportConfig =
        RabbitReportConfig()
    private var deviceInfoStr = ""
    private val REQUEST_THREAD = Executors.newFixedThreadPool(
        1
    ) { r -> Thread(r, "rabbit_report_request_thread") }

    private val dataEmitterTask = RabbitReportDataEmitterTask()
    private val LOAD_POINT_TO_EMITER_QUEUE = 1
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                LOAD_POINT_TO_EMITER_QUEUE -> {
                    val loadDataCount = RabbitReportDataEmitterTask.EMITER_QUEUE_MAX_SIZE / 2
                    RabbitDbStorageManager.getAll(
                        RabbitReportInfo::class.java,
                        count = loadDataCount,
                        sortField = "time",
                        loadResult = {
                            dataEmitterTask.addPointsToEmitterQueue(it)
                            RabbitLog.d(TAG, "load point from db! Emitter Start")
                            startEmitterTask()
                        })
                }
            }
        }
    }

    fun init(app: Application, config: RabbitReportConfig) {
        application = app
        mConfig = config

        config.notReportDataFormat.add(RabbitReportInfo::class.java)

        RabbitAsync.asyncRun({
            deviceInfoStr = Gson().toJson(getDeviceInfo(application))
            RabbitLog.d(TAG, "device info : $deviceInfoStr")
        })

        dataEmitterTask.eventListener = object : RabbitReportDataEmitterTask.EventListener {
            override fun successEmitterPoint(pointInfo: RabbitReportInfo) {
                RabbitDbStorageManager.delete(
                    RabbitReportInfo::class.java,
                    condition = Pair(
                        com.susion.rabbit.base.greendao.RabbitReportInfoDao.Properties.Time,
                        pointInfo.time.toString()
                    )
                )
            }

            override fun pointQueueIsEmpty() {
                val currentDbPointCount =
                    RabbitDbStorageManager.dataCount(RabbitReportInfo::class.java)
                RabbitLog.d(TAG, "pointQueueIsEmpty db point : $currentDbPointCount")
                if (currentDbPointCount > 0) {
                    mHandler.sendEmptyMessage(LOAD_POINT_TO_EMITER_QUEUE)
                }
            }
        }
    }

    /**
     * @param useTime  应用使用的总时长
     * */
    fun report(info: Any, useTime: Long = 0) {

        //暴露给外界的数据上报接口
        mConfig.dataReportListener?.onPrepareReportData(info, useTime)

        if (!mConfig.enable) return

        if (mConfig.notReportDataFormat.contains(info.javaClass)) return

        val reportInfo = RabbitReportTransformCenter.createReportInfo(info, useTime)

        RabbitLog.d(TAG, "report  ${reportInfo.type} data  use time : $useTime")

        RabbitDbStorageManager.save(reportInfo)

        dataEmitterTask.addPoint(reportInfo)

        startEmitterTask()
    }

    private fun startEmitterTask() {
        if (!dataEmitterTask.isRunning) {
            REQUEST_THREAD.execute {
                dataEmitterTask.emitterPoints()
            }
        }
    }

    private fun getDeviceInfo(application: Application): RabbitDeviceInfo {
        val roomInfo = com.susion.rabbit.base.common.RomUtils.getRomInfo()
        val supportCpuAbi = StringBuilder()
        Build.SUPPORTED_ABIS.forEachIndexed { index, abi ->
            if (index != 0) {
                supportCpuAbi.append("/")
            }
            supportCpuAbi.append(abi)
        }
        return RabbitDeviceInfo().apply {
            deviceName = DeviceUtils.getDeviceName()
            deviceId = DeviceUtils.getDeviceId(application)
            systemVersion = Build.VERSION.RELEASE
            memorySize = DeviceUtils.getMemorySize(application)
            rom = "${roomInfo.name}/${roomInfo.version}"
            appVersionCode = "${DeviceUtils.getAppVersionCode(application)}"
            isRoot = DeviceUtils.isDeviceRooted()
            supportAbi = supportCpuAbi.toString()
            manufacturer = Build.MANUFACTURER
        }
    }

    fun getDeviceInfoStr() = deviceInfoStr

}