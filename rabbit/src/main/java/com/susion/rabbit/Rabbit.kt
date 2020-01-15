package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.view.View
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.base.config.RabbitCustomConfigProtocol
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.base.ui.RabbitUiEvent
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.utils.FloatingViewPermissionHelper
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.tracer.RabbitPluginConfig
import com.susion.rabbit.ui.RabbitUi
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 * 直接使用这个类中的API来操控 Rabbit
 */
object Rabbit : RabbitProtocol {

    private var mConfig = RabbitConfig()
    lateinit var application: Application
    private var isInit = false

    override fun init(config: RabbitConfig) {

        try {
            if (!RabbitUtils.isMainProcess(application)) return
        } catch (e: Throwable) {
            RabbitLog.d("init error :${e.message}")
            return
        }

        mConfig = config

        if (!mConfig.enable) {
            return
        }

        //加载 gradle plugin init
        RabbitPluginConfig.loadConfig()

        // init log
        RabbitLog.isEnable = mConfig.enableLog

        //存储配置
        RabbitStorage.addEventListener(object : RabbitStorage.EventListener {
            override fun onStorageData(obj: Any) {
                RabbitReport.report(obj, RabbitMonitor.getAppUseTimes())
            }
        })
        RabbitStorage.init(application, mConfig.storageConfig)

        //监控配置
        RabbitMonitor.eventListener = object : RabbitMonitor.UiEventListener {
            override fun updateUi(type: Int, value: Any) {
                RabbitUi.refreshFloatingViewUi(type, value)
            }
        }
        RabbitMonitor.init(application, mConfig.monitorConfig)

        //上报配置
        val reportConfig = mConfig.reportConfig
        reportConfig.enable = true
        reportConfig.notReportDataFormat.apply {
            add(RabbitMemoryInfo::class.java)
            add(RabbitHttpLogInfo::class.java)
        }
        reportConfig.fpsReportPeriodS = mConfig.monitorConfig.fpsReportPeriodS
        RabbitReport.init(application, reportConfig)

        // UI
        val uiConfig = mConfig.uiConfig
        uiConfig.monitorList = RabbitMonitor.getMonitorList()
        uiConfig.entryFeatures.addAll(RabbitUi.defaultSupportFeatures(application))
        uiConfig.customConfigList.addAll(getCustomConfigs())
        RabbitUi.init(application, uiConfig)
        RabbitUi.eventListener = object : RabbitUi.EventListener {

            override fun getGlobalConfig() = mConfig

            override fun changeGlobalMonitorStatus(open: Boolean) {
                val monitorName = RabbitMonitorProtocol.GLOBAL_MONITOR.name
                RabbitUi.refreshFloatingViewUi(RabbitUiEvent.CHANGE_GLOBAL_MONITOR_STATUS, open)
                RabbitSettings.setAutoOpenFlag(application, monitorName, open)
            }

            override fun toggleMonitorStatus(monitor: RabbitMonitorProtocol, open: Boolean) {
                val monitorName = monitor.getMonitorInfo().name
                if (open) {
                    RabbitMonitor.openMonitor(monitorName)
                } else {
                    RabbitMonitor.closeMonitor(monitorName)
                }
            }
        }
        //全局监控模式的特殊处理
        val autoOpen = RabbitSettings.autoOpen(application, RabbitMonitorProtocol.GLOBAL_MONITOR.name)
        if (autoOpen){
            RabbitUi.refreshFloatingViewUi(RabbitUiEvent.CHANGE_GLOBAL_MONITOR_STATUS, true)
        }

        isInit = true
        RabbitLog.d("init success!!")
    }

    override fun reConfig(config: RabbitConfig) {
        mConfig = config
    }

    /**
     * 需要Activity Window Token来展示Dialog
     * */
    override fun open(requestPermission: Boolean, activity: Activity) {

        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(application)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            RabbitUiKernal.showFloatingView()
        } else {
            FloatingViewPermissionHelper.showConfirmDialog(
                activity,
                object : FloatingViewPermissionHelper.OnConfirmResult {
                    override fun confirmResult(confirm: Boolean) {
                        if (confirm) {
                            FloatingViewPermissionHelper.tryStartFloatingWindowPermission(
                                application
                            )
                        }
                    }
                })
        }
    }

    /**
     * 网络请求日志功能
     * */
    override fun getNetInterceptor(): Interceptor = RabbitMonitor.getNetMonitor()

    /**
     * 异常日志保存
     * */
    override fun saveCrashLog(e: Throwable) {
        RabbitMonitor.saveCrash(e, Thread.currentThread())
    }

    override fun isAutoOpen() = RabbitSettings.autoOpenRabbit(application)

    override fun changeAutoOpenStatus(autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(application, autoOpen)
    }

    override fun openPage(pageClass: Class<out View>?, params: Any?) {
        RabbitUi.openPage(pageClass, params)
    }

    override fun getCurrentActivity() = RabbitUi.getCurrentActivity()

    /**
     * 自定义的开关配置
     * */
    private fun getCustomConfigs(): List<RabbitCustomConfigProtocol> {
        return ArrayList<RabbitCustomConfigProtocol>().apply {
            add(
                RabbitCustomConfigProtocol(
                    "上报监控数据",
                    mConfig.reportConfig.enable,
                    statusChangeCallBack = object :
                        RabbitCustomConfigProtocol.ConfigChangeListener {
                        override fun onChange(newStatus: Boolean) {
                            mConfig.reportConfig.enable = newStatus
                        }
                    })
            )

            add(
                RabbitCustomConfigProtocol(
                    "Log开关",
                    RabbitLog.isEnable,
                    object : RabbitCustomConfigProtocol.ConfigChangeListener {
                        override fun onChange(newStatus: Boolean) {
                            RabbitLog.isEnable = newStatus
                        }
                    })
            )
        }
    }

}