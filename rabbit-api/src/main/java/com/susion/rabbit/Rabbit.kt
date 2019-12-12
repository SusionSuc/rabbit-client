package com.susion.rabbit

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 * Rabbit 入口类
 */
object Rabbit {

    private var mConfig = RabbitConfig()
    lateinit var application: Application
    private var isInit = false

    private val applicationLifecycle = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            RabbitUi.showFloatingView()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            RabbitUi.hideFloatingView()
            RabbitUi.hideAllPage()
        }
    }

    /**
     * Rabbit 初始化入口 [MUST CALL]
     * Rabbit UI展现基于WindowManager
     * 对于Rabbit支持的自定义配置看 [RabbitConfig]
     * 打开Rabbit悬浮球调用 [openDevTools]
     * */
    @JvmStatic
    fun init(config: RabbitConfig = RabbitConfig()) {
        if (!isMainProcess(application)) return
        mConfig = config
        RabbitUi.init(application, mConfig.uiConfig)
        RabbitLog.init(mConfig.enableLog)
        RabbitReport.init(application, mConfig.reportConfig)
        RabbitStorage.init(application, mConfig.storageConfig)
        RabbitMonitor.init(application, mConfig.monitorConfig)
        RabbitMonitor.eventListener = object : RabbitMonitor.UiEventListener {
            override fun updateUi(type: Int, value: Any) {
                RabbitUi.updateUiFromAsyncThread(type, value)
            }
        }
        isInit = true
        RabbitLog.d("init success!!")
    }

    private fun listenLifeCycle() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(applicationLifecycle)
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycle)
    }

    fun openDevTools(requestPermission: Boolean = true, context: Context = application!!) {

        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(application!!)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            listenLifeCycle()
            RabbitUi.showFloatingView()
        } else {
            FloatingViewPermissionHelper.showConfirmDialog(context,
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
    fun getHttpLogInterceptor(): Interceptor = RabbitMonitor.getNetMonitor()

    fun getConfig() = mConfig

    /**
     * 异常日志保存
     * */
    fun saveCrashLog(e: Throwable) {
        RabbitMonitor.saveCrash(e, Thread.currentThread())
    }

    fun destroy() {
        RabbitDbStorageManager.destroy()
    }

    /**
     * 悬浮球是否在展示
     * */
    fun isOpen() = isInit

    fun autoOpen(context: Context) = RabbitSettings.autoOpenRabbit(context)

    fun enableAutoOpen(autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(application, autoOpen)
    }

    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(
            context
        )
    }

    private fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }


}