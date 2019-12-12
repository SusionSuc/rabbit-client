package com.susion.rabbit.monitor

import android.app.Application
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.RabbitSettings
import com.susion.rabbit.RabbitMonitorProtocol
import com.susion.rabbit.monitor.instance.*
import com.susion.rabbit.monitor.instance.RabbitAppSpeedMonitor
import com.susion.rabbit.monitor.instance.RabbitBlockMonitor
import com.susion.rabbit.monitor.instance.RabbitFPSMonitor
import com.susion.rabbit.monitor.instance.RabbitMemoryMonitor
import okhttp3.Interceptor

/**
 * susionwang at 2019-10-18
 * 整个Rabbit的监控系统
 */
object RabbitMonitor {

    private val TAG = "rabbit-monitor"
    var application: Application? = null
    private var initStatus = false
    var config: Config = Config()
    var eventListener: UiEventListener? = null
    private val monitorMap = HashMap<String, RabbitMonitorProtocol>()

    fun init(context: Application, config: Config) {
        if (initStatus) return

        this.config = config
        application = context
        initStatus = true

        initMonitor()

        this.config.autoOpenMonitors.forEach {
            RabbitSettings.setAutoOpenFlag(context, it, true)
        }

        monitorMap.values.forEach {
            val autoOpen = RabbitSettings.autoOpen(context, it.getMonitorInfo().name)
            if (autoOpen) {
                it.open(context)
                RabbitLog.d(TAG, "monitor auto open : ${it.getMonitorInfo().name} ")
            }
        }
    }

    private fun initMonitor() {
        monitorMap.apply {
            put(RabbitMonitorProtocol.APP_SPEED.name, RabbitAppSpeedMonitor())
            put(RabbitMonitorProtocol.FPS.name, RabbitFPSMonitor())
            put(RabbitMonitorProtocol.BLOCK.name, RabbitBlockMonitor())
            put(RabbitMonitorProtocol.MEMORY.name, RabbitMemoryMonitor())
            put(RabbitMonitorProtocol.EXCEPTION.name, RabbitExceptionMonitor())
            put(RabbitMonitorProtocol.NET.name, RabbitNetMonitor())
        }
    }

    fun openMonitor(name: String) {
        assertInit()
        monitorMap[name]?.open(application!!)
    }

    fun closeMonitor(name: String) {
        assertInit()
        monitorMap[name]?.close()
    }

    fun isOpen(name: String): Boolean {
        return monitorMap[name]?.isOpen ?: false
    }

    private fun assertInit() {
        if (!initStatus) {
            throw RuntimeException("RabbitMonitorProtocol not call open!")
        }
    }

    fun monitorRequest(requestUrl: String): Boolean {
        val appSpeedMonitor = monitorMap[RabbitMonitorProtocol.APP_SPEED.name]
        if (appSpeedMonitor is RabbitAppSpeedMonitor) {
            return appSpeedMonitor.monitorRequest(requestUrl)
        }
        return false
    }

    fun markRequestFinish(requestUrl: String, costTime: Long) {
        val appSpeedMonitor = monitorMap[RabbitMonitorProtocol.APP_SPEED.name]
        if (appSpeedMonitor is RabbitAppSpeedMonitor) {
            appSpeedMonitor.markRequestFinish(requestUrl, costTime)
        }
    }

    fun isAutoOpen(monitor: RabbitMonitorProtocol): Boolean {
        if (application == null) return false
        return RabbitSettings.autoOpen(application!!, monitor.getMonitorInfo().name)
    }

    fun getMonitorList() = ArrayList<RabbitMonitorProtocol>().apply {
        addAll(monitorMap.values)
    }

    fun saveCrash(e: Throwable, thread: Thread) {
        getMonitor<RabbitExceptionMonitor>()?.saveCrash(e, thread)
    }

    fun getNetMonitor(): Interceptor {
        return getMonitor<RabbitNetMonitor>() ?: RabbitNetMonitor()
    }

    private inline fun <reified T : RabbitMonitorProtocol> getMonitor(): T? {
        for (monitor in monitorMap.values) {
            if (monitor is T) {
                return monitor as T
            }
        }
        return null
    }

    /**
     * @property blockStackCollectPeriod 卡顿栈采集周期
     * @property blockThreshold  卡顿检测阈值, 即卡顿多长时间算一次卡顿
     * @property autoOpenMonitors 自动打开的监控功能, name 取自 [com.susion.rabbit.performance.core.RabbitMonitor]
     * for example : [com.susion.rabbit.performance.core.RabbitMonitor.BLOCK.enName]
     * @property memoryValueCollectPeriod 多长时间采集一次内存状态
     * */
    class Config(
        var blockStackCollectPeriod: Long = STANDARD_FRAME_NS,
        var blockThreshold: Long = STANDARD_FRAME_NS * 10,
        var autoOpenMonitors: List<String> = ArrayList(),
        var memoryValueCollectPeriod: Long = 2000L
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }

    interface UiEventListener {
        fun updateUi(type: Int, value: Any)
    }

}