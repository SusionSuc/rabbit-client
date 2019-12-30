package com.susion.rabbit.base


/**
 * susionwang at 2019-12-03
 */
interface RabbitMonitorProtocol {

    companion object {
        val APP_SPEED = MonitorInfo(
            "app_speed_monitor",
            "应用测速",
            true
        ) //应用测速: app冷启动 & 页面测速
        val BLOCK = MonitorInfo(
            "block_monitor",
            "卡顿监控",
            true
        ) //卡顿监控
        val FPS = MonitorInfo(
            "fps_monitor",
            "FPS监控",
            true
        ) //fps监控
        val MEMORY = MonitorInfo(
            "memory_monitor",
            "内存监控",
            true
        ) //内存监控
        val TRAFFIC = MonitorInfo(
            "traffic_monitor",
            "流量监控",
            true
        ) //网络监控
        val EXCEPTION = MonitorInfo(
            "exception_monitor",
            "异常监控",
            true
        )
        val NET =
            MonitorInfo("net_monitor", "网络监控", true)
        val USE_TIME = MonitorInfo(
            "use_time",
            "使用时长监控",
            true,
            showInExternal = false
        )
    }

    class MonitorInfo(
        val name: String,
        val znName: String,
        val runInDebug: Boolean = true,
        val showInExternal: Boolean = true
    )
}