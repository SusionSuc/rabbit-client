package com.susion.rabbit.monitor

import android.app.Activity
import android.app.Application
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.monitor.instance.*
import com.susion.rabbit.monitor.instance.RabbitAppSpeedMonitor
import com.susion.rabbit.monitor.instance.RabbitBlockMonitor
import com.susion.rabbit.monitor.instance.RabbitFPSMonitor
import com.susion.rabbit.monitor.instance.RabbitMemoryMonitor
import okhttp3.Interceptor
import java.lang.ref.WeakReference

/**
 * susionwang at 2019-10-18
 * 整个Rabbit的监控系统
 */
object RabbitMonitor {

    private val TAG = javaClass.simpleName
    var application: Application? = null
    private var isInit = false
    var config: Config = Config()
    var eventListener: UiEventListener? = null
    private val monitorMap = HashMap<String, RabbitMonitorProtocol>()
    private var appCurrentActivity: WeakReference<Activity?>? = null    //当前应用正在展示的Activity
    private var pageChangeListeners = HashSet<PageChangeListener>()

    fun init(application: Application, config: Config) {
        if (isInit) return

        this.config = config
        this.application = application
        config.autoOpenMonitors.add(RabbitMonitorProtocol.USE_TIME.name)

        application.registerActivityLifecycleCallbacks(object : com.susion.rabbit.base.common.RabbitActivityLifecycleWrapper() {
            override fun onActivityResumed(activity: Activity?) {
                appCurrentActivity = WeakReference(activity)
                pageChangeListeners.forEach { it.onPageShow() }
            }
        })

        //所有的监控类型
        monitorMap.apply {
            put(RabbitMonitorProtocol.APP_SPEED.name, RabbitAppSpeedMonitor())
            put(RabbitMonitorProtocol.FPS.name, RabbitFPSMonitor())
            put(RabbitMonitorProtocol.BLOCK.name, RabbitBlockMonitor())
            put(RabbitMonitorProtocol.MEMORY.name, RabbitMemoryMonitor())
            put(RabbitMonitorProtocol.EXCEPTION.name, RabbitExceptionMonitor())
            put(RabbitMonitorProtocol.NET.name, RabbitNetMonitor())
            put(RabbitMonitorProtocol.USE_TIME.name, RabbitAppUseTimeMonitor())
            put(RabbitMonitorProtocol.METHOD_TRACE.name, RabbitMethodMonitor())
        }

        this.config.autoOpenMonitors.forEach {
            RabbitSettings.setAutoOpenFlag(application, it, true)
        }

        monitorMap.values.forEach {
            val autoOpen = RabbitSettings.autoOpen(application, it.getMonitorInfo().name)
            if (autoOpen) {
                it.open(application)
                RabbitLog.d(TAG, "monitor auto open : ${it.getMonitorInfo().name} ")
            }
        }

        isInit = true
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
        if (!isInit) {
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

    fun getCurrentPage() = appCurrentActivity?.get()?.javaClass?.name ?: ""

    fun addPageChangeListener(listener: PageChangeListener) {
        pageChangeListeners.add(listener)
    }

    fun removePageChangeListener(listener: PageChangeListener) {
        pageChangeListeners.remove(listener)
    }

    private inline fun <reified T : RabbitMonitorProtocol> getMonitor(): T? {
        for (monitor in monitorMap.values) {
            if (monitor is T) {
                return monitor
            }
        }
        return null
    }

    fun getAppUseTimes(): Long {
        return getMonitor<RabbitAppUseTimeMonitor>()?.getAppUseTimeS() ?: 0L
    }

    /**
     * @property blockStackCollectPeriodNs 卡顿栈采集周期
     * @property blockThresholdNs  卡顿检测阈值, 即卡顿多长时间算一次卡顿
     * @property autoOpenMonitors
     *      自动打开的监控功能, name 取自 [com.susion.rabbit.performance.core.RabbitMonitor]
     *      for example : [com.susion.rabbit.performance.core.RabbitMonitor.BLOCK.enName]
     * @property memoryValueCollectPeriodMs 多长时间采集一次内存状态
     * @property fpsCollectThresholdNs fps采集周期，即多长时间计算一次FPS
     * @property fpsReportPeriodS 上报FPS信息的周期, 用户与页面交互的累计时间。 10 还是挺长的 ！
     * */
    class Config(
        var blockStackCollectPeriodNs: Long = STANDARD_FRAME_NS,
        var blockThresholdNs: Long = STANDARD_FRAME_NS * 10,
        var autoOpenMonitors: HashSet<String> = HashSet(),
        var memoryValueCollectPeriodMs: Long = 2000L,
        var fpsCollectThresholdNs: Long = STANDARD_FRAME_NS * 10,
        var fpsReportPeriodS: Long = 10,
        var slowMethodPeriodMs:Long = 500
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }

    interface UiEventListener {
        fun updateUi(type: Int, value: Any)
    }

    interface PageChangeListener {
        fun onPageShow()
    }

}