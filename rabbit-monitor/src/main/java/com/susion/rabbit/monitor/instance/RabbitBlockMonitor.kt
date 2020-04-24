package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.common.toastInThread
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.ChoreographerMonitorCenter
import com.susion.rabbit.monitor.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.storage.RabbitStorage
import java.util.concurrent.TimeUnit

/**
 * 监控应用卡顿
 * */
internal open class RabbitBlockMonitor(override var isOpen: Boolean = false) :
    ChoreographerFrameUpdateMonitor.FrameUpdateListener, RabbitMonitorProtocol {

    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashMap<String, RabbitBlockStackTraceInfo>()

    // 栈采集周期
    private val stackCollectPeriod = TimeUnit.MILLISECONDS.convert(
        RabbitMonitor.mConfig.blockStackCollectPeriodNs,
        TimeUnit.NANOSECONDS
    )
    private val blockThreshold = RabbitMonitor.mConfig.blockThresholdNs

    private var monitorThread: HandlerThread? = null
    private var collectCount = 0

    //采集一次主线程堆栈, 【随机的， 因此并不一定是卡顿点, 不过抓住卡顿点的概率很大】
    private val blockStackCollectTask = object : Runnable {
        override fun run() {
            collectCount++
            if (collectCount > 1) {
                val stackTrace = RabbitBlockStackTraceInfo(getUiThreadStackTrace())
                val mapKey = stackTrace.getMapKey()
                val info = blockStackTraces[mapKey]
                if (info == null) {
                    blockStackTraces[mapKey] = stackTrace
                } else {
                    info.collectCount++
                }
            }
            stackCollectHandler?.postDelayed(this, stackCollectPeriod)
        }
    }

    override fun open(context: Context) {
        monitorThread = HandlerThread("rabbit_block_monitor")
        monitorThread!!.start()
        stackCollectHandler = Handler(monitorThread!!.looper)

        ChoreographerMonitorCenter.addSimpleFrameUpdateListener(this)
        isOpen = true
    }

    override fun close() {
        monitorThread?.quitSafely()
        ChoreographerMonitorCenter.removeSimpleFrameUpdateListener(this)
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.BLOCK

    override fun doFrame(frameCostNs: Long) {
        stackCollectHandler?.removeCallbacks(blockStackCollectTask)
        if (frameCostNs > blockThreshold && blockStackTraces.isNotEmpty()) {
            toastInThread("检测到卡顿!!", RabbitMonitor.application)
            val blockFrameInfo = RabbitBlockFrameInfo()
            val traceList = blockStackTraces.values.toList()
            blockFrameInfo.apply {
                costTime = frameCostNs
                blockFrameStrackTraceStrList = Gson().toJson(traceList)
                blockIdentifier = RabbitUtils.getBlockStackIdentifierByMaxCount(traceList)
                time = System.currentTimeMillis()
                pageName = RabbitMonitor.getCurrentPage()
            }
            RabbitStorage.save(blockFrameInfo)
        }

        stackCollectHandler?.postDelayed(blockStackCollectTask, stackCollectPeriod)

        blockStackTraces.clear()
    }

    //在某个线程不断地获取主线程堆栈要暂停主线程的运行
    private fun getUiThreadStackTrace(): String {
        return RabbitUtils.traceToString(0, Looper.getMainLooper().thread.stackTrace)
    }

}