package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.RabbitUiEvent
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.LazyChoreographerFrameUpdateMonitor
import com.susion.rabbit.RabbitMonitorProtocol
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
internal class RabbitFPSMonitor(override var isOpen: Boolean = false) : LazyChoreographerFrameUpdateMonitor.FrameUpdateListener,
    RabbitMonitorProtocol {

    private val TAG = javaClass.simpleName
    private val frameTracer = LazyChoreographerFrameUpdateMonitor().apply {
        init()
    }

    //1帧时间的阈值
    private var frameIntervalNs: Long = 16666666
    private val FPS_COLLECT_PERIOD = frameIntervalNs * 10  // 10 帧采集一次帧率
    private var totalFrameNs: Long = 0
    private var totalFrameNumber: Long = 0
    private var lastTotalFrameNs: Long = 0
    private var lastTotalFrameNumber: Long = 0

    fun init() {
        frameTracer.addFrameUpdateListener(this)
    }

    override fun open(context: Context) {
        frameTracer.addFrameUpdateListener(this)
        frameTracer.startMonitor()
        isOpen = true
    }

    override fun close() {
        frameTracer.removeFrameUpdateListener(this)
        frameTracer.stopMonitor()
        RabbitMonitor.eventListener?.updateUi(RabbitUiEvent.MSG_UPDATE_FPS, 0f)
        isOpen = false
    }


    override fun getMonitorInfo() = RabbitMonitorProtocol.FPS

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
            RabbitMonitor.eventListener?.updateUi(RabbitUiEvent.MSG_UPDATE_FPS, fps)
            lastTotalFrameNs = totalFrameNs
            lastTotalFrameNumber = totalFrameNumber
        }
    }

}