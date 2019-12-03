package com.susion.rabbit.performance.core

import android.content.Context

/**
 * susionwang at 2019-12-03
 */
interface RabbitMonitor {

    companion object {
        val APP_SPEED = MonitorInfo("app_speed_monitor", "应用测速", true) //应用测速: app冷启动 & 页面测速
        val BLOCK = MonitorInfo("block_monitor", "卡顿监控", true) //卡顿监控
        val FPS = MonitorInfo("fps_monitor", "FPS监控", true) //fps监控
        val MEMORY = MonitorInfo("memory_monitor", "内存监控", true) //fps监控
    }

    class MonitorInfo(val enName: String, val znName: String, val runInDebug: Boolean = true)

    fun open(context: Context)

    fun close()

    fun getMonitorInfo(): MonitorInfo

    fun isOpen(): Boolean

}