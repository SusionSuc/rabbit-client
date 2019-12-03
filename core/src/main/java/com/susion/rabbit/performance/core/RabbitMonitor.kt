package com.susion.rabbit.performance.core

import android.content.Context

/**
 * susionwang at 2019-12-03
 */
interface RabbitMonitor {

    companion object {
        val MONITOR_APP_SPEED = MonitorInfo("app_speed_monitor", "应用测速") //应用测速: app冷启动 & 页面测速
        val MONITOR_BLOCK = MonitorInfo("block_monitor", "卡顿监控") //卡顿监控
        val MONITOR_FPS = MonitorInfo("fps_monitor", "FPS监控") //fps监控
    }

    class MonitorInfo(val enName: String, val znName: String)

    fun open(context: Context)

    fun close()

    fun getMonitorInfo(): MonitorInfo

    fun isOpen(): Boolean

}