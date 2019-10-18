package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.os.Bundle
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.exception.RabbitExceptionLogStorageManager
import com.susion.rabbit.net.RabbitHttpLogInterceptor
import com.susion.rabbit.net.RabbitHttpLogStorageManager
import com.susion.rabbit.trace.RabbitTracer
import com.susion.rabbit.utils.RabbitSettings
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import com.susion.rabbit.utils.toastInThread
import com.susion.rabbit.view.RabbitFloatingView
import okhttp3.Interceptor
import java.lang.ref.WeakReference

/**
 * susionwang at 2019-09-23
 */
object Rabbit {

    private var mConfig = RabbitFeatureConfig()

    var application: Application? = null

    //是否已经打开 Rabbit
    private var isInDevModel = false

    //当前是否处于DevTools的页面中
    var isInDevToolsPage = false

    private val acLifecycleListener = SimpleAcLifecycleListener()
    private val devToolsAcList = ArrayList<WeakReference<RabbitBaseActivity>>()

    private val applicationLifecycle = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            floatingView.show()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            floatingView.hide()
        }
    }

    private val httpLogInterceptor by lazy {
        RabbitHttpLogInterceptor()
    }

    private val floatingView by lazy {
        RabbitFloatingView(application!!)
    }

    //MUST CALL
    fun attachApplicationContext(applicationContext: Application) {
        application = applicationContext
        openGlobalExceptionCollector()
        RabbitTracer.init()
    }

    private fun listenLifeCycle() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(applicationLifecycle)
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycle)
    }

    fun devToolsIsOpen() = isInDevModel

    fun setDevToolsOpenStatus(isOpen: Boolean) {
        isInDevModel = isOpen
    }

    fun openDevTools(requestPermission: Boolean = true, context: Context = application!!) {

        application?.registerActivityLifecycleCallbacks(acLifecycleListener)

        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(application!!)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            listenLifeCycle()
            floatingView.show()
            RabbitSettings.autoOpenDevTools(context, true)  //default auto open
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

    //网络请求日志功能
    fun getHttpLogInterceptor(): Interceptor = httpLogInterceptor

    /**
     * 业务配置 Rabbit
     * */
    fun config(devConfig: RabbitFeatureConfig) {
        mConfig = devConfig
    }

    fun getCustomConfig() = mConfig

    //收集所有线程的崩溃信息
    private fun openGlobalExceptionCollector() {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrashLog(throwable)
            defaultExceptionHandler.uncaughtException(thread, throwable)
        }
    }

    //异常日志功能
    fun saveCrashLog(e: Throwable) {
        toastInThread("发生异常！,日志已保存到本地")
        RabbitExceptionLogStorageManager.saveExceptionToLocal(e)
    }

    fun quickFinishAllDevToolsPage() {
        devToolsAcList.forEach {
            it.get()?.finish()
        }
    }

    fun autoOpenDevTools(context: Context) = RabbitSettings.autoOpenDevTools(context)

    fun destroy() {
        RabbitHttpLogStorageManager.destroy()
        RabbitExceptionLogStorageManager.destroy()
    }

    private open class SimpleAcLifecycleListener : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity?) {

        }

        override fun onActivityStarted(activity: Activity?) {

        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is RabbitBaseActivity) {
                devToolsAcList.add(WeakReference(activity))
            }
        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity is RabbitBaseActivity) {
                devToolsAcList.remove(WeakReference(activity))
            }
        }

        override fun onActivityPaused(activity: Activity?) {
        }
    }

}