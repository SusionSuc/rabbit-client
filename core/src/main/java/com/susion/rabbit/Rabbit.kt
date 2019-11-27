package com.susion.rabbit

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.os.Bundle
import com.susion.rabbit.config.RabbitConfig
import com.susion.rabbit.config.RabbitSettings
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.exception.RabbitExceptionManager
import com.susion.rabbit.net.RabbitHttpLogInterceptor
import com.susion.rabbit.tracer.RabbitTracer
import com.susion.rabbit.tracer.monitor.RabbitAppSpeedInterceptor
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import com.susion.rabbit.utils.RabbitActivityLifecycleWrapper
import okhttp3.Interceptor
import java.lang.ref.WeakReference

/**
 * susionwang at 2019-09-23
 */
object Rabbit {

    private var mConfig = RabbitConfig()
    var application: Application? = null
    private var isInit = false
    val uiManager by lazy { RabbitUiManager(application!!) }

    //当前应用正在展示的Activity
    var appCurrentActivity: WeakReference<Activity?>? = null

    private val applicationLifecycle = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            uiManager.showFloatingView()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            uiManager.hideFloatingView()
            uiManager.hideAllPage()
        }
    }

    private val httpLogInterceptor by lazy {
        RabbitHttpLogInterceptor()
    }

    /**
     * Rabbit 初始化入口 [MUST CALL]
     * Rabbit UI展现基于WindowManager, Rabbit 中的Context不能用来展示如[Dialog]之类的UI
     * 对于Rabbit支持的自定义配置看 [RabbitConfig]
     * 打开Rabbit悬浮球调用 [openDevTools]
     * */
    @JvmStatic
    fun init(applicationContext: Application, config_: RabbitConfig = RabbitConfig()) {
        application = applicationContext
        mConfig = config_
        applicationContext.registerActivityLifecycleCallbacks(object :
            RabbitActivityLifecycleWrapper(){
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                appCurrentActivity = WeakReference(activity)
            }
        })
        RabbitExceptionManager.openGlobalExceptionCollector()
        RabbitTracer.init(applicationContext, mConfig.traceConfig)
        RabbitDbStorageManager.clearOldSessionData()
        isInit = true
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
            uiManager.showFloatingView()
        } else {
            FloatingViewPermissionHelper.showConfirmDialog(context,
                object : FloatingViewPermissionHelper.OnConfirmResult {
                    override fun confirmResult(confirm: Boolean) {
                        if (confirm) {
                            FloatingViewPermissionHelper.tryStartFloatingWindowPermission(
                                application!!
                            )
                        }
                    }
                })
        }
    }

    /**
     * 网络请求日志功能
     * */
    fun getHttpLogInterceptor(): Interceptor = httpLogInterceptor

    fun getApiTracerInterceptor() = RabbitAppSpeedInterceptor()

    fun geConfig() = mConfig

    /**
     * 异常日志保存
     * */
    fun saveCrashLog(e: Throwable) {
        RabbitExceptionManager.saveCrash(e, Thread.currentThread())
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
        if (application == null) return
        RabbitSettings.autoOpenRabbit(application!!, autoOpen)
    }

}