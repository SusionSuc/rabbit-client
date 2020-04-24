package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitStorage

/**
 * susionwang at 2020-01-14
 * 全局性能监控模式
 */
internal class RabbitAppPerformanceMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private var appPerformanceInfo: RabbitAppPerformanceInfo? = null
    private val dataStorageListener = object : RabbitStorage.EventListener {
        override fun onStorageData(obj: Any) {
            if (appPerformanceInfo == null) return
            relatedIdToAppPerformanceInfo(obj)
        }
    }

    override fun close() {
        saveMonitorInfo(false)
        appPerformanceInfo = null
        RabbitStorage.removeEventListener(dataStorageListener)
    }

    override fun open(context: Context) {
        appPerformanceInfo = RabbitAppPerformanceInfo()
        appPerformanceInfo?.time = System.currentTimeMillis()
        appPerformanceInfo?.endTime = System.currentTimeMillis()
        RabbitStorage.addEventListener(dataStorageListener)
    }

    private fun relatedIdToAppPerformanceInfo(obj: Any) {
        when (obj) {
            is RabbitFPSInfo -> {
                appPerformanceInfo?.fpsIds = "${appPerformanceInfo?.fpsIds ?: ""}${obj.id}&"
            }
            is RabbitMemoryInfo -> {
                appPerformanceInfo?.memoryIds = "${appPerformanceInfo?.memoryIds ?: ""}${obj.id}&"
            }
            is RabbitAppStartSpeedInfo -> {
                appPerformanceInfo?.appStartId = obj.id.toString()
            }
            is RabbitPageSpeedInfo -> {
                appPerformanceInfo?.pageSpeedIds =
                    "${appPerformanceInfo?.pageSpeedIds ?: ""}${obj.id}&"
            }
            is RabbitBlockFrameInfo -> {
                appPerformanceInfo?.blockIds = "${appPerformanceInfo?.blockIds ?: ""}${obj.id}&"
            }
            is RabbitSlowMethodInfo -> {
                appPerformanceInfo?.slowMethodIds =
                    "${appPerformanceInfo?.slowMethodIds ?: ""}${obj.id}&"
            }
        }

        saveMonitorInfo(true)
    }

    private fun saveMonitorInfo(isRunning: Boolean = false) {
        if (appPerformanceInfo != null) {
            appPerformanceInfo?.isRunning = isRunning
            appPerformanceInfo?.endTime = System.currentTimeMillis()
            RabbitStorage.updateOrCreate(
                RabbitAppPerformanceInfo::class.java,
                appPerformanceInfo!!,
                appPerformanceInfo?.id ?: 0
            )
        }
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.GLOBAL_MONITOR

}