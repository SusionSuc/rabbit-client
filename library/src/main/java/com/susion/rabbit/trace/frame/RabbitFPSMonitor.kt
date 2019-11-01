package com.susion.rabbit.trace.frame

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.gson.Gson
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.trace.RabbitExecuteManager
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.toastInThread
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
class RabbitFPSMonitor : FrameTracer.FrameUpdateListener {

    private val TAG = javaClass.simpleName

    //1帧时间的阈值
    private var frameIntervalNs: Long = 16666666
    private val FPS_COLLECT_PERIOD = frameIntervalNs * 10  // 10 帧采集一次帧率
    private var totalFrameNs: Long = 0
    private var totalFrameNumber: Long = 0
    private var lastTotalFrameNs: Long = 0
    private var lastTotalFrameNumber: Long = 0
    private val stackCollectPeriod = TimeUnit.MILLISECONDS.convert(frameIntervalNs, TimeUnit.NANOSECONDS) * 3

    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashSet<RabbitBlockStackTraceInfo>()
    //一帧采集一次主线程堆栈
    private val blockStackCollectTask = object :Runnable{
        override fun run() {
            blockStackTraces.add(RabbitBlockStackTraceInfo(getUiThreadStackTrace()))
            stackCollectHandler?.postDelayed(this, stackCollectPeriod)
        }
    }

    fun init() {
        val sampleThread = HandlerThread("rabbit_fps_monitor")
        sampleThread.start()
        stackCollectHandler = Handler(sampleThread.looper)
    }

    override fun doFrame(
        frameCostNs: Long,
        inputCostNs: Long,
        animationCostNs: Long,
        traversalCostNs: Long
    ) {

        fpsCalculate(frameCostNs)

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

        if (frameCostNs > frameIntervalNs * 10 && blockStackTraces.isNotEmpty()) {  //卡了 10 帧 以上
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

    /**
     * 计算当前的帧率
     * */
    private fun fpsCalculate(frameCostNs: Long) {
        if (frameCostNs > frameIntervalNs){
            RabbitLog.d("trace", "frameCostNs : $frameCostNs")
        }

        val costUnitFrameNumber = (frameCostNs / frameIntervalNs) + 1

        totalFrameNs += (costUnitFrameNumber * frameIntervalNs)
        totalFrameNumber += 1

        val durationNs = totalFrameNs - lastTotalFrameNs
        val collectFrame = totalFrameNumber - lastTotalFrameNumber

        if (durationNs >= FPS_COLLECT_PERIOD) {
            val fps = min(60f, collectFrame * 6f)
            Rabbit.uiManager.updateUiFromAsynThread(RabbitUiManager.MSA_UPDATE_FPS, fps)
            lastTotalFrameNs = totalFrameNs
            lastTotalFrameNumber = totalFrameNumber
        }

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