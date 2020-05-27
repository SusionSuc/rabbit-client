package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.base.config.RabbitCustomConfigProtocol
import com.susion.rabbit.base.config.RabbitUiConfig
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
 */

object Rabbit : RabbitProtocol {

    private var mConfig = RabbitConfig()
    lateinit var application: Application
    private var isInit = false

    override fun init(app: Application, config: RabbitConfig) {

        try {
            if (!RabbitUtils.isMainProcess(app)) return
        } catch (e: Throwable) {
            RabbitLog.d("init error :${e.message}")
            return
        }

        application = app
        mConfig = config

        if (!mConfig.enable || isInit) {
            return
        }

        RabbitLog.isEnable = mConfig.enableLog

        configStorage()

        configMonitor()

        configReport()

        configUi()

        initAllComponent()

        isInit = true

        RabbitLog.d("init success!!")
    }

    private fun configStorage() {
        RabbitStorage.addEventListener(object : RabbitStorage.EventListener {
            override fun onStorageData(obj: Any) {
                RabbitReport.report(obj, RabbitMonitor.getAppUseTimes())
            }
        })
    }

    private fun configMonitor() {
        //加载 gradle plugin init
        RabbitPluginConfig.loadConfig()
        RabbitMonitor.uiEventListener = object : RabbitMonitor.UIEventListener {
            override fun updateUi(type: Int, value: Any) {
                RabbitUi.refreshFloatingViewUi(type, value)
            }
        }
    }

    private fun configReport() {
        val reportConfig = mConfig.reportConfig
        reportConfig.notReportDataFormat.apply {
            add(RabbitMemoryInfo::class.java)
            add(RabbitHttpLogInfo::class.java)
        }
        reportConfig.fpsReportPeriodS = mConfig.monitorConfig.fpsReportPeriodS
    }

    private fun configUi(): RabbitUiConfig {
        val uiConfig = mConfig.uiConfig
        uiConfig.monitorList = RabbitMonitor.getMonitorList()
        uiConfig.entryFeatures.addAll(RabbitUi.defaultSupportFeatures(application))
        uiConfig.customConfigList.addAll(getDefaultCustomConfigs())
        RabbitUi.eventListener = object : RabbitUi.EventListener {

            override fun closeAllMonitor() {
                RabbitMonitor.closeAllMonitor()
            }

            override fun getGlobalConfig() = mConfig

            override fun changeGlobalMonitorStatus(open: Boolean) {
                val monitorName = RabbitMonitorProtocol.GLOBAL_MONITOR.name
                RabbitSettings.setAutoOpenFlag(application, monitorName, open)
                if (!open) {
                    RabbitUi.refreshFloatingViewUi(
                        RabbitUiEvent.CHANGE_GLOBAL_MONITOR_STATUS,
                        false
                    )
                    RabbitMonitor.closeMonitor(monitorName)
                }
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
        return uiConfig
    }

    private fun initAllComponent() {
        RabbitStorage.init(application, mConfig.storageConfig)
        RabbitReport.init(application, mConfig.reportConfig)
        RabbitUi.init(application, mConfig.uiConfig)
        RabbitMonitor.init(application, mConfig.monitorConfig)
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

    override fun isAutoOpen(context: Context) = RabbitSettings.autoOpenRabbit(context)

    override fun changeAutoOpenStatus(context: Context, autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(context, autoOpen)
    }

    override fun openPage(pageClass: Class<out View>?, params: Any?) {
        RabbitUi.openPage(pageClass, params)
    }

    override fun getCurrentActivity() = RabbitUi.getCurrentActivity()

    /**
     * 自定义的开关配置
     * */
    private fun getDefaultCustomConfigs(): List<RabbitCustomConfigProtocol> {
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