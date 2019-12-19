package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.gson.Gson
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.toastInThread
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.util.concurrent.TimeUnit

/**
 * 监控应用卡顿
 * */
internal class RabbitBlockMonitor(override var isOpen: Boolean = false) : ChoreographerFrameUpdateMonitor.FrameUpdateListener,
    RabbitMonitorProtocol {

    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashMap<String, RabbitBlockStackTraceInfo>()

    // 栈采集周期
    private val stackCollectPeriod = TimeUnit.MILLISECONDS.convert(
        RabbitMonitor.config.blockStackCollectPeriodNs,
        TimeUnit.NANOSECONDS
    )
    private val blockThreshold = RabbitMonitor.config.blockThresholdNs

    private var monitorThread: HandlerThread? = null
    private val frameTracer = ChoreographerFrameUpdateMonitor()

    //采集一次主线程堆栈, 【随机的， 因此并不一定是卡顿点, 不过抓住卡顿点的概率很大】
    private val blockStackCollectTask = object : Runnable {
        override fun run() {
            val stackTrace =
                RabbitBlockStackTraceInfo(getUiThreadStackTrace())
            val mapKey = stackTrace.getMapKey()
            val info = blockStackTraces[mapKey]
            if (info == null) {
                blockStackTraces[mapKey] = stackTrace
            } else {
                info.collectCount++
            }
            stackCollectHandler?.postDelayed(this, stackCollectPeriod)
        }
    }


    override fun open(context: Context) {
        monitorThread = HandlerThread("rabbit_block_monitor")
        monitorThread!!.start()
        stackCollectHandler = Handler(monitorThread!!.looper)

        frameTracer.addFrameUpdateListener(this)
        frameTracer.startMonitorFrame()
        isOpen = true
    }

    override fun close() {
        monitorThread?.quitSafely()
        frameTracer.removeFrameUpdateListener(this)
        frameTracer.stopMonitorFrame()
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.BLOCK

    override fun doFrame(frameCostNs: Long) {
        stackCollectHandler?.removeCallbacks(blockStackCollectTask)
        if (frameCostNs > blockThreshold && blockStackTraces.isNotEmpty()) {
            toastInThread("检测到卡顿!!", RabbitMonitor.application)
            val blockFrameInfo = com.susion.rabbit.base.entities.RabbitBlockFrameInfo()
            blockFrameInfo.apply {
                costTime = frameCostNs
                blockFrameStrackTraceStrList = Gson().toJson(blockStackTraces.values.toList())
                blockIdentifier = getIdentifierByMaxCount(blockStackTraces)
                time = System.currentTimeMillis()
                blockPage = RabbitMonitor.getCurrentPage()
            }
            RabbitDbStorageManager.save(blockFrameInfo)
        }

        stackCollectHandler?.postDelayed(blockStackCollectTask, stackCollectPeriod)

        blockStackTraces.clear()
    }

    private fun getUiThreadStackTrace(): String {
        return traceToString(0, Looper.getMainLooper().thread.stackTrace)
    }

    private fun traceToString(skipStackCount: Int, stackArray: Array<StackTraceElement>): String {
        if (stackArray.isEmpty()) {
            return "[]"
        }

        val b = StringBuilder()
        for (i in 0 until stackArray.size - skipStackCount) {
            if (i == stackArray.size - skipStackCount - 1) {
                return b.toString()
            }
            b.append(stackArray[i])
            b.append("\n")
        }
        return b.toString()
    }

    private fun getIdentifierByMaxCount(traceMap: Map<String, RabbitBlockStackTraceInfo>): String {
        return traceMap.values.toList().maxBy { it.collectCount }?.stackTrace.toString()
    }

}