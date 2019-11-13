package com.susion.rabbit

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import com.susion.rabbit.config.RabbitConfig
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.exception.RabbitExceptionManager
import com.susion.rabbit.net.RabbitHttpLogInterceptor
import com.susion.rabbit.tracer.RabbitTracer
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 */
object Rabbit {

    private var mConfig = RabbitConfig()
    var application: Application? = null
    private var isInit = false
    val uiManager by lazy { RabbitUiManager(application!!) }

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
     *
     * 打开Rabbit悬浮球调用 [openDevTools]
     * */
    fun init(applicationContext: Application, config_: RabbitConfig = RabbitConfig()) {
        application = applicationContext
        mConfig = config_
        RabbitExceptionManager.openGlobalExceptionCollector()
        RabbitTracer.init(applicationContext)
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

}