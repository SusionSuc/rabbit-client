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
import com.susion.rabbit.trace.RabbitTracer
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 */
object Rabbit {

    private var mConfig = RabbitConfig()
    var application: Application? = null
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

    //MUST CALL
    fun attachApplicationContext(applicationContext: Application) {
        application = applicationContext
        RabbitExceptionManager.openGlobalExceptionCollector()
        RabbitTracer.init(applicationContext)
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

    /**
     * 业务配置 Rabbit
     * */
    fun config(devConfig: RabbitConfig) {
        mConfig = devConfig
    }

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

    fun isOpen() = uiManager.floatingViewIsShow

}