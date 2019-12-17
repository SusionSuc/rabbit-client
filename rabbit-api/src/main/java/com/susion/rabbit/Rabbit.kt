package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.content.Context
import com.susion.rabbit.common.RabbitUtils
import com.susion.rabbit.entities.RabbitHttpLogInfo
import com.susion.rabbit.entities.RabbitMemoryInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.utils.FloatingViewPermissionHelper
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
    fun init(config: RabbitConfig = RabbitConfig()) {
        if (!RabbitUtils.isMainProcess(application)) return

        mConfig = config

        RabbitLog.init(mConfig.enableLog)

        //上报配置
        val reportConfig = mConfig.reportConfig
        reportConfig.reportMonitorData = true
        reportConfig.notReportDataFormat.apply {
            add(RabbitMemoryInfo::class.java)
            add(RabbitHttpLogInfo::class.java)
        }
        RabbitReport.init(application, reportConfig)

        //存储配置
        RabbitStorage.init(application, mConfig.storageConfig)
        RabbitStorage.eventListener = object : RabbitStorage.EventListener {
            override fun onStorageData(obj: Any) {
                RabbitReport.report(obj, RabbitMonitor.getAppUseTimes())
            }
        }

        //监控配置
        RabbitMonitor.init(application, mConfig.monitorConfig)
        RabbitMonitor.eventListener = object : RabbitMonitor.UiEventListener {
            override fun updateUi(type: Int, value: Any) {
                RabbitUi.updateUiFromAsyncThread(type, value)
            }
        }

        //ui 配置
        val uiConfig = mConfig.uiConfig
        uiConfig.monitorList = RabbitMonitor.getMonitorList()
        RabbitUi.init(application, uiConfig)
        RabbitUi.eventListener = object : RabbitUi.EventListener {
            override fun toggleMonitorStatus(monitor: RabbitMonitorProtocol, open: Boolean) {
                val monitorName = monitor.getMonitorInfo().name
                if (open) {
                    RabbitMonitor.openMonitor(monitorName)
                } else {
                    RabbitMonitor.closeMonitor(monitorName)
                }
            }
        }

        isInit = true
        RabbitLog.d("init success!!")
    }

    /**
     * 需要Activity Token来展示Dialog
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
                            FloatingViewPermissionHelper.tryStartFloatingWindowPermission(application)
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