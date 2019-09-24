package com.susion.devtools

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import com.susion.devtools.net.DevToolsHttpLogInterceptor
import com.susion.devtools.utils.DevToolsSettings
import com.susion.devtools.utils.FloatingViewPermissionHelper
import com.susion.devtools.view.FloatingView
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 */
object DevTools {

    private var application: Application? = null

    private val httpLogInterceptor by lazy {
        DevToolsHttpLogInterceptor()
    }

    private val floatingView by lazy {
        FloatingView(application!!)
    }

    fun init(application_: Application) {
        application = application_
        listenLifeCycle()
        tryShowDevFloatingView(application_)
    }

    private fun listenLifeCycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onForeground() {
                showFloatingView(application!!)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onBackground() {
                hideFloatingView()
            }
        })
    }

    fun showFloatingView(context: Context) {
        if (application == null) return

        if (!FloatingViewPermissionHelper.checkPermission(context)) {
            FloatingViewPermissionHelper.tryStartFloatingWindowPermission(context)
            return
        }

        floatingView.show()
    }

    private fun hideFloatingView() {
        if (application == null) return
        floatingView.hide()
    }

    fun tryShowDevFloatingView(context: Context) {
        if (DevToolsSettings.autoShowFloatingView(context)) {
            showFloatingView(context)
        }
    }

    fun autoShowDevBtn(context: Context) {
        DevToolsSettings.autoShowFloatingView(context)
    }

    fun getHttpLogInterceptor():Interceptor = httpLogInterceptor

}