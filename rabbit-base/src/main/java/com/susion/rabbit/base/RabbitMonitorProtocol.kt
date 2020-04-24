package com.susion.rabbit.base

import android.content.Context

/**
 * susionwang at 2019-12-03
 */
interface RabbitMonitorProtocol {

    companion object {

        //应用测速: app冷启动 & 页面测速
        val APP_SPEED = MonitorInfo(
            "app_speed",
            "应用测速"
        )

        //卡顿
        val BLOCK = MonitorInfo(
            "block",
            "卡顿监控"
        )

        //卡顿监控
        val FPS = MonitorInfo(
            "fps",
            "FPS监控"
        )

        //fps监控
        val MEMORY = MonitorInfo(
            "memory",
            "内存监控"
        )

        //应用流量使用健康
        val TRAFFIC = MonitorInfo(
            "traffic",
            "流量监控"
        )

        //网络监控
        val EXCEPTION = MonitorInfo(
            "exception",
            "异常捕获"
        )

        //网络日志监控
        val NET = MonitorInfo("net", "网络监控")

        val USE_TIME = MonitorInfo(
            "use_time",
            "使用时长监控",
            showInExternal = false,
            dataCanClear = false
        )

        //慢函数
        val SLOW_METHOD = MonitorInfo(
            "slow_method",
            "慢函数"
        )

        //阻塞调用
        val BLOCK_CALL = MonitorInfo("code_scan", "代码扫描")

        val GLOBAL_MONITOR = MonitorInfo("global_mode", "性能测试", showInExternal = false)

        val ANR = MonitorInfo("anr", "ANR监控")

        val THREAD = MonitorInfo("thread", "线程分析")

    }

    class MonitorInfo(
        val name: String,
        val znName: String,
        val showInExternal: Boolean = true,
        val dataCanClear: Boolean = true
    )

    fun open(context: Context)

    fun close()

    fun getMonitorInfo(): MonitorInfo

    var isOpen: Boolean


}