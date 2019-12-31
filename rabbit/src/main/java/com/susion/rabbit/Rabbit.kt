package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.base.utils.FloatingViewPermissionHelper
import com.susion.rabbit.ui.monitor.RabbitMonitorUi
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 * Rabbit API
 */
object Rabbit {

    private var mConfig = RabbitConfig()
    lateinit var application: Application
    private var isInit = false

    @JvmStatic
    fun config(config: RabbitConfig) {
        try {
            if (!RabbitUtils.isMainProcess(application)) return
        } catch (e: Throwable) {
            RabbitLog.d("config error :${e.message}")
            return
        }

        mConfig = config

        RabbitLog.init(mConfig.enableLog)

        //存储配置
        RabbitStorage.eventListener = object : RabbitStorage.EventListener {
            override fun onStorageData(obj: Any) {
                RabbitReport.report(obj, RabbitMonitor.getAppUseTimes())
            }
        }
        RabbitStorage.init(application, mConfig.storageConfig)

        //监控配置
        RabbitMonitor.eventListener = object : RabbitMonitor.UiEventListener {
            override fun updateUi(type: Int, value: Any) {
                RabbitUi.updateUiFromAsyncThread(type, value)
            }
        }
        RabbitMonitor.init(application, mConfig.monitorConfig)

        //上报配置
        val reportConfig = mConfig.reportConfig
        reportConfig.reportMonitorData = true
        reportConfig.notReportDataFormat.apply {
            add(RabbitMemoryInfo::class.java)
            add(RabbitHttpLogInfo::class.java)
        }
        reportConfig.fpsReportPeriodS = mConfig.monitorConfig.fpsReportPeriodS
        RabbitReport.init(application, reportConfig)

        // 监控UI
        val monitorUiConfig = RabbitMonitorUi.Config()
        monitorUiConfig.monitorList = RabbitMonitor.getMonitorList()
        RabbitMonitorUi.init(application, monitorUiConfig)
        RabbitMonitorUi.eventListener = object : RabbitMonitorUi.EventListener {
            override fun toggleMonitorStatus(monitor: RabbitMonitorProtocol, open: Boolean) {
                val monitorName = monitor.getMonitorInfo().name
                if (open) {
                    RabbitMonitor.openMonitor(monitorName)
                } else {
                    RabbitMonitor.closeMonitor(monitorName)
                }
            }
        }

        //基本UI
        val uiConfig = mConfig.uiConfig
        uiConfig.entryFeatures.addAll(RabbitMonitorUi.defaultSupportFeatures())
        RabbitUi.init(application, uiConfig)

        isInit = true
        RabbitLog.d("config success!!")
    }

    /**
     * 需要Activity Window Token来展示Dialog
     * */
    fun open(requestPermission: Boolean = true, activity: Activity) {

        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(application)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            RabbitUi.showFloatingView()
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
    fun getNetInterceptor(): Interceptor = RabbitMonitor.getNetMonitor()

    /**
     * 异常日志保存
     * */
    fun saveCrashLog(e: Throwable) {
        RabbitMonitor.saveCrash(e, Thread.currentThread())
    }

    fun autoOpen(context: Context) = RabbitSettings.autoOpenRabbit(context)

    fun enableAutoOpen(autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(application, autoOpen)
    }

}