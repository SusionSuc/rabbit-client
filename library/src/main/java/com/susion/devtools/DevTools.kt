package com.susion.devtools

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.os.Bundle
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.exception.ExceptionLogStorageManager
import com.susion.devtools.net.DevToolsHttpLogInterceptor
import com.susion.devtools.net.HttpLogStorageManager
import com.susion.devtools.utils.DevToolsSettings
import com.susion.devtools.utils.FloatingViewPermissionHelper
import com.susion.devtools.utils.toastInThread
import com.susion.devtools.view.FloatingView
import okhttp3.Interceptor
import java.lang.ref.WeakReference

/**
 * susionwang at 2019-09-23
 */
object DevTools {

    var application: Application? = null

    //是否已经打开 DevTools
    private var isInDevModel = false

    //当前是否处于DevTools的页面中
    var isInDevToolsPage = false

    private val acLifecycleListener = SimpleAcLifecycleListener()
    private val devToolsAcList = ArrayList<WeakReference<DevToolsBaseActivity>>()

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
        DevToolsHttpLogInterceptor()
    }

    private val floatingView by lazy {
        FloatingView(application!!)
    }

    fun init(application_: Application) {
        application = application_
        if (DevToolsSettings.autoOpenDevTools(application_)) {
            openDevTools(false)
        }
        application?.registerActivityLifecycleCallbacks(acLifecycleListener)
        openGlobalExceptionCollector()
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

        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(application!!)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            listenLifeCycle()
            floatingView.show()
            DevToolsSettings.autoOpenDevTools(context, true)  //default auto open
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
        ExceptionLogStorageManager.saveExceptionToLocal(e)
    }

    fun quickFinishAllDevToolsPage() {
        devToolsAcList.forEach {
            it.get()?.finish()
        }
    }

    fun autoOpenDevTools(context: Context) = DevToolsSettings.autoOpenDevTools(context)

    fun destroy() {
        HttpLogStorageManager.destroy()
        ExceptionLogStorageManager.destroy()
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
            if (activity is DevToolsBaseActivity) {
                devToolsAcList.add(WeakReference(activity))
            }
        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity is DevToolsBaseActivity) {
                devToolsAcList.remove(WeakReference(activity))
            }
        }

        override fun onActivityPaused(activity: Activity?) {
        }
    }


}