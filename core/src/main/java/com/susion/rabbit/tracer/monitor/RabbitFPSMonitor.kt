package com.susion.rabbit.tracer.monitor

import com.susion.rabbit.Rabbit
import com.susion.rabbit.tracer.core.LazyChoreographerFrameUpdateMonitor
import com.susion.rabbit.ui.RabbitUiManager
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
class RabbitFPSMonitor : LazyChoreographerFrameUpdateMonitor.FrameUpdateListener {

    private val TAG = javaClass.simpleName

    //1帧时间的阈值
    private var frameIntervalNs: Long = 16666666
    private val FPS_COLLECT_PERIOD = frameIntervalNs * 10  // 10 帧采集一次帧率
    private var totalFrameNs: Long = 0
    private var totalFrameNumber: Long = 0
    private var lastTotalFrameNs: Long = 0
    private var lastTotalFrameNumber: Long = 0

    fun init() {}

    override fun doFrame(
        frameCostNs: Long,
        inputCostNs: Long,
        animationCostNs: Long,
        traversalCostNs: Long
    ) {
        fpsCalculate(frameCostNs)
    }

    /**
     * 计算当前的帧率
     * */
    private fun fpsCalculate(frameCostNs: Long) {

        val costUnitFrameNumber = (frameCostNs / frameIntervalNs) + 1

        totalFrameNs += (costUnitFrameNumber * frameIntervalNs)
        totalFrameNumber += 1

        val durationNs = totalFrameNs - lastTotalFrameNs
        val collectFrame = totalFrameNumber - lastTotalFrameNumber

        if (durationNs >= FPS_COLLECT_PERIOD) {
            val fps = min(60f, collectFrame * 6f)
            Rabbit.uiManager.updateUiFromAsyncThread(RabbitUiManager.MSA_UPDATE_FPS, fps)
            lastTotalFrameNs = totalFrameNs
            lastTotalFrameNumber = totalFrameNumber
        }
    }

}