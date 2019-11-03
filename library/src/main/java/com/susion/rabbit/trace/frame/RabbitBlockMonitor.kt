package com.susion.rabbit.trace.frame

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.gson.Gson
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.trace.RabbitExecuteManager
import com.susion.rabbit.trace.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.trace.entities.RabbitBlockFrameInfo
import com.susion.rabbit.trace.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.utils.toastInThread

/**
 * 监控应用卡顿
 * */
class RabbitBlockMonitor : ChoreographerFrameUpdateMonitor.FrameUpdateListener {

    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashSet<RabbitBlockStackTraceInfo>()

    private val stackCollectPeriod = 30L     // 栈采集周期
    private val blockThreshold = 200L       //卡顿阈值

    //一帧采集一次主线程堆栈
    private val blockStackCollectTask = object : Runnable {
        override fun run() {
            blockStackTraces.add(
                RabbitBlockStackTraceInfo(
                    getUiThreadStackTrace()
                )
            )
            stackCollectHandler?.postDelayed(this, stackCollectPeriod)
        }
    }

    fun init() {
        val sampleThread = HandlerThread("rabbit_block_monitor")
        sampleThread.start()
        stackCollectHandler = Handler(sampleThread.looper)
    }

    override fun doFrame(
        frameCostNs: Long,
        inputCostNs: Long,
        animationCostNs: Long,
        traversalCostNs: Long
    ) {
        captureBlockInfo(frameCostNs, inputCostNs, animationCostNs, traversalCostNs)
    }

    /**
     * 捕获卡顿信息
     * */
    private fun captureBlockInfo(
        frameCostNs: Long,
        inputCostNs: Long,
        animationCostNs: Long,
        traversalCostNs: Long
    ) {

        stackCollectHandler?.removeCallbacks(blockStackCollectTask)

        if (frameCostNs > blockThreshold && blockStackTraces.isNotEmpty()) {
            toastInThread("检测到卡顿!!")
            val blockFrameInfo = RabbitBlockFrameInfo()
            blockFrameInfo.apply {
                costTime = frameCostNs
                inputEventCostNs = inputCostNs
                animationEventCostNs = animationCostNs
                traversalEventCostNs = traversalCostNs
                blockFrameStrackTraceStrList = Gson().toJson(blockStackTraces.toList())
                blockIdentifier = blockStackTraces.first().stackTrace
                time = System.currentTimeMillis()
            }
            RabbitExecuteManager.DB_THREAD.execute {
                RabbitDbStorageManager.save(blockFrameInfo)
            }
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

}