package com.susion.rabbit.monitor

import android.app.Activity
import android.app.Application
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.common.RabbitActivityLifecycleWrapper
import com.susion.rabbit.base.config.RabbitMonitorConfig
import com.susion.rabbit.base.entities.RabbitAppSpeedMonitorConfig
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

    var application: Application? = null
    private var isInit = false
    var config: RabbitMonitorConfig = RabbitMonitorConfig()
    var eventListener: UiEventListener? = null
    private val monitorMap = HashMap<String, RabbitMonitorProtocol>()
    private var appCurrentActivity: WeakReference<Activity?>? = null    //当前应用正在展示的Activity
    private var pageChangeListeners = HashSet<PageChangeListener>()

    fun init(application: Application, config: RabbitMonitorConfig) {

        if (isInit) return

        this.config = config
        this.application = application
        config.autoOpenMonitors.add(RabbitMonitorProtocol.USE_TIME.name)

        application.registerActivityLifecycleCallbacks(object : RabbitActivityLifecycleWrapper() {
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
            put(RabbitMonitorProtocol.BLOCK_CALL.name, RabbitIoCallMonitor())
        }

        this.config.autoOpenMonitors.forEach {
            RabbitSettings.setAutoOpenFlag(application, it, true)
        }

        monitorMap.values.forEach {
            val autoOpen = RabbitSettings.autoOpen(application, it.getMonitorInfo().name)
            if (autoOpen) {
                it.open(application)
                RabbitLog.d(TAG_MONITOR, "monitor auto open : ${it.getMonitorInfo().name} ")
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

    fun configMonitorSpeedList(speedConfig: RabbitAppSpeedMonitorConfig) {
        getMonitor<RabbitAppSpeedMonitor>()?.configMonitorList(speedConfig)
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

    interface UiEventListener {
        fun updateUi(type: Int, value: Any)
    }

    interface PageChangeListener {
        fun onPageShow()
    }

}