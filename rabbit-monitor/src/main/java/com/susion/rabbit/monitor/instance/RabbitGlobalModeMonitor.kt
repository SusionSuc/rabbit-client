package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.storage.RabbitStorage

/**
 * susionwang at 2020-01-14
 *
 * 全局监控模式
 */
class RabbitGlobalModeMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private var globalMonitorInfo: RabbitGlobalMonitorInfo? = null
    private val dataStorageListener = object : RabbitStorage.EventListener {
        override fun onStorageData(obj: Any) {
            if (globalMonitorInfo == null) return
            relatedIdToGlobalMonitorInfo(obj)
        }
    }

    override fun close() {
        globalMonitorInfo = null
        RabbitStorage.removeEventListener(dataStorageListener)
    }

    override fun open(context: Context) {
        globalMonitorInfo = RabbitGlobalMonitorInfo()
        globalMonitorInfo?.time = System.currentTimeMillis()
        globalMonitorInfo?.endTime = System.currentTimeMillis()
        RabbitStorage.addEventListener(dataStorageListener)
    }

    private fun relatedIdToGlobalMonitorInfo(obj: Any) {
        RabbitLog.d(
            TAG_MONITOR,
            "RabbitGlobalModeMonitor relatedIdToGlobalMonitorInfo -> ${obj.javaClass.simpleName}"
        )
        when (obj) {
            is RabbitFPSInfo -> {
                globalMonitorInfo?.fpsIds = "${globalMonitorInfo?.fpsIds ?: ""}${obj.id}&"
            }
            is RabbitMemoryInfo -> {
                globalMonitorInfo?.memoryIds = "${globalMonitorInfo?.memoryIds ?: ""}${obj.id}&"
            }
            is RabbitAppStartSpeedInfo -> {
                globalMonitorInfo?.appStartId = obj.id.toString()
            }
            is RabbitPageSpeedInfo -> {
                globalMonitorInfo?.pageSpeedIds =
                    "${globalMonitorInfo?.pageSpeedIds ?: ""}${obj.id}&"
            }
            is RabbitBlockFrameInfo -> {
                globalMonitorInfo?.blockIds = "${globalMonitorInfo?.blockIds ?: ""}${obj.id}&"
            }
            is RabbitSlowMethodInfo -> {
                globalMonitorInfo?.slowMethodIds =
                    "${globalMonitorInfo?.slowMethodIds ?: ""}${obj.id}&"
            }
        }

        if (globalMonitorInfo != null) {
            globalMonitorInfo?.isRunning = true
            globalMonitorInfo?.endTime = System.currentTimeMillis()
            RabbitDbStorageManager.updateOrCreate(
                RabbitGlobalMonitorInfo::class.java,
                globalMonitorInfo!!,
                globalMonitorInfo?.id ?: 0
            )
        }
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.GLOBAL_MONITOR

}