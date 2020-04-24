package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.LazyChoreographerFrameUpdateMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.config.RabbitMonitorConfig
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.RabbitUiEvent
import com.susion.rabbit.monitor.core.ChoreographerMonitorCenter
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
internal class RabbitFPSMonitor(override var isOpen: Boolean = false) :
    LazyChoreographerFrameUpdateMonitor.FrameUpdateListener,
    RabbitMonitorProtocol {

    private var frameIntervalNs: Long = RabbitMonitorConfig.STANDARD_FRAME_NS
    private val FPS_COLLECT_PERIOD_NS = RabbitMonitor.mConfig.fpsCollectThresholdNs
    private val FPS_COLLECT_NUMBER = (TimeUnit.NANOSECONDS.convert(
        RabbitMonitor.mConfig.fpsReportPeriodS,
        TimeUnit.SECONDS
    )) / (RabbitMonitorConfig.STANDARD_FRAME_NS)  // 多少帧记录一次数据
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
        ChoreographerMonitorCenter.addDetailedFrameUpdateListener(this)
        RabbitMonitor.addPageChangeListener(pageShowedListener)
        isOpen = true
    }

    override fun close() {
        ChoreographerMonitorCenter.removeDetailedFrameUpdateListener(this)
        RabbitMonitor.uiEventListener?.updateUi(RabbitUiEvent.MSG_UPDATE_FPS, 0f)
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

        if (durationNs >= FPS_COLLECT_PERIOD_NS) {
            val radio =
                TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS) * 1.0f / FPS_COLLECT_PERIOD_NS
            val fps = min(60f, collectFrame * radio)
            RabbitMonitor.uiEventListener?.updateUi(RabbitUiEvent.MSG_UPDATE_FPS, fps)
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
        val currentPageName = RabbitMonitor.getCurrentPage()
        val monitorPkgList = RabbitMonitor.mConfig.fpsMonitorPkgList
        if (monitorPkgList.isNotEmpty() && !RabbitUtils.classInPkgList(
                currentPageName,
                monitorPkgList
            )
        ) return
//        RabbitLog.d(TAG_MONITOR, "saveFpsInfo ---> current page name : $currentPageName")
        val fpsInfo = RabbitFPSInfo()
        fpsInfo.maxFps = fpsList.max() ?: 0
        fpsInfo.minFps = fpsList.min() ?: 0
        fpsInfo.avgFps = fpsList.average().toInt()
        fpsInfo.pageName = currentPageName
        fpsInfo.time = System.currentTimeMillis()
        RabbitStorage.save(fpsInfo)
        fpsList.clear()
    }

}