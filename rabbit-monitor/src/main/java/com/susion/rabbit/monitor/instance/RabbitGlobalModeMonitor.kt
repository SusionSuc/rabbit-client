package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.base.entities.RabbitGlobalMonitorInfo
import com.susion.rabbit.storage.RabbitStorage

/**
 * susionwang at 2020-01-14
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
        RabbitStorage.addEventListener(dataStorageListener)
    }

    private fun relatedIdToGlobalMonitorInfo(obj: Any) {
        when (obj) {
            is RabbitFPSInfo -> {
                RabbitLog.d(TAG_MONITOR, "fps relatedIdToGlobalMonitorInfo ${obj.id}")
                globalMonitorInfo?.fpsIds = "${globalMonitorInfo?.fpsIds}${obj.id}&"
            }
        }
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.GLOBAL_MONITOR

}