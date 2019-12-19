package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.RabbitUiEvent
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.LazyChoreographerFrameUpdateMonitor
import com.susion.rabbit.RabbitMonitorProtocol
import com.susion.rabbit.entities.RabbitFPSInfo
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
internal class RabbitFPSMonitor(override var isOpen: Boolean = false) :
    LazyChoreographerFrameUpdateMonitor.FrameUpdateListener,
    RabbitMonitorProtocol {

    private val TAG = javaClass.simpleName
    private val frameTracer = LazyChoreographerFrameUpdateMonitor().apply {
        init()
    }

    private var frameIntervalNs: Long = RabbitMonitor.Config.STANDARD_FRAME_NS
    private val FPS_COLLECT_PERIOD = RabbitMonitor.config.fpsCollectThresholdNs
    private val FPS_COLLECT_NUMBER = (TimeUnit.NANOSECONDS.convert(
        RabbitMonitor.config.fpsReportPeriodS,
        TimeUnit.SECONDS
    )) / (RabbitMonitor.Config.STANDARD_FRAME_NS)  // 多少帧记录一次数据
    private var totalFrameNs: Long = 0
    private var totalFrameNumber: Long = 0
    private var lastTotalFrameNs: Long = 0
    private var lastTotalFrameNumber: Long = 0
    private val fpsList = ArrayList<Int>()
    private val pageShowedListener = object : RabbitMonitor.PageChangeListener {
        override fun onPageShow() {
            saveFpsInfo()
        }
    }

    override fun open(context: Context) {
        frameTracer.addFrameUpdateListener(this)
        frameTracer.startMonitor()
        RabbitMonitor.addPageChangeListener(pageShowedListener)
        isOpen = true
    }

    override fun close() {
        frameTracer.removeFrameUpdateListener(this)
        frameTracer.stopMonitor()
        RabbitMonitor.eventListener?.updateUi(RabbitUiEvent.MSG_UPDATE_FPS, 0f)
        RabbitMonitor.removePageChangeListener(pageShowedListener)
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
            fpsList.add(fps.toInt())
        }

        if (totalFrameNumber > FPS_COLLECT_NUMBER && totalFrameNumber % FPS_COLLECT_NUMBER == 0L) {
            saveFpsInfo()
        }
    }

    //持久化fps采集的信息
    @Synchronized
    private fun saveFpsInfo() {
        if (fpsList.isEmpty()) return
        RabbitLog.d(TAG, "saveFpsInfo --->")
        val fpsInfo = RabbitFPSInfo()
        fpsInfo.maxFps = fpsList.max() ?: 0
        fpsInfo.minFps = fpsList.min() ?: 0
        fpsInfo.avgFps = fpsList.average().toInt()
        fpsInfo.pageName = RabbitMonitor.getCurrentPage()
        fpsInfo.time = System.currentTimeMillis()
        RabbitDbStorageManager.save(fpsInfo)
        fpsList.clear()
    }

}