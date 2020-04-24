package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.ChoreographerMonitorCenter
import com.susion.rabbit.monitor.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.storage.RabbitStorage
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2020-03-17
 *
 * android 21 以上监控 ANR
 *
 * 开启一个子线程来监控主线程是否发生ANR
 */
internal class RabbitANRHighVersionMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol, ChoreographerFrameUpdateMonitor.FrameUpdateListener {

    private var monitorThread: HandlerThread? = null
    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashMap<String, RabbitBlockStackTraceInfo>()
    private val stackCollectPeriod = RabbitMonitor.mConfig.anrStackCollectPeriodNs
    private val stackPostDelayTime =
        TimeUnit.MILLISECONDS.convert(stackCollectPeriod, TimeUnit.NANOSECONDS)
    private val anrCheckPeriodNs = RabbitMonitor.mConfig.anrCheckPeriodNs
    private var collectCount = 0

    //采集一次主线程堆栈, 【随机的， 因此并不一定是卡顿点, 不过抓住卡顿点的概率很大】
    private val blockStackCollectTask = object : Runnable {
        override fun run() {
            if (collectCount > 1) {
                captureMainThreadStack()   // 避免频繁抓取主线程堆栈
            }
            collectCount++
            if (collectCount * stackCollectPeriod >= anrCheckPeriodNs) {
                Toast.makeText(RabbitMonitor.application, "检测到ANR!!", Toast.LENGTH_SHORT).show()
                val anrInfo = RabbitAnrInfo()
                anrInfo.apply {
                    stackStr = Gson().toJson(blockStackTraces.values.toList())
                    time = System.currentTimeMillis()
                    pageName = RabbitMonitor.getCurrentPage()
                    invalid = true
                }
                RabbitStorage.save(anrInfo)
                stackCollectHandler?.removeCallbacks(this)
            } else {
                stackCollectHandler?.postDelayed(this, stackPostDelayTime)
            }
        }
    }

    //在某个线程不断地获取主线程堆栈要暂停主线程的运行
    private fun captureMainThreadStack() {
        val stackStr = RabbitUtils.traceToString(0, Looper.getMainLooper().thread.stackTrace)
        val stackTrace = RabbitBlockStackTraceInfo(stackStr)
        val mapKey = stackTrace.getMapKey()
        val info = blockStackTraces[mapKey]
        if (info == null) {
            blockStackTraces[mapKey] = stackTrace
        } else {
            info.collectCount++
        }
    }

    override fun open(context: Context) {
        monitorThread = HandlerThread("rabbit_anr_monitor")
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

    override fun doFrame(frameCostNs: Long) {
        stackCollectHandler?.removeCallbacks(blockStackCollectTask)
        collectCount = 0
        blockStackTraces.clear()
        stackCollectHandler?.postDelayed(blockStackCollectTask, stackPostDelayTime)
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.ANR

}