package com.susion.rabbit

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import com.susion.rabbit.exception.RabbitExceptionLogStorageManager
import com.susion.rabbit.net.RabbitHttpLogInterceptor
import com.susion.rabbit.net.RabbitHttpLogStorageManager
import com.susion.rabbit.trace.RabbitTracer
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.FloatingViewPermissionHelper
import com.susion.rabbit.utils.RabbitSettings
import com.susion.rabbit.utils.toastInThread
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 */
object Rabbit {

    private var mConfig = RabbitFeatureConfig()

    var application: Application? = null

    val uiManager by lazy {
        RabbitUiManager(application!!)
    }

    private val applicationLifecycle = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            uiManager.showFloatingView()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            uiManager.hideFloatingView()
        }
    }

    private val httpLogInterceptor by lazy {
        RabbitHttpLogInterceptor()
    }

    //MUST CALL
    fun attachApplicationContext(applicationContext: Application) {
        application = applicationContext
        openGlobalExceptionCollector()
        RabbitTracer.init()
        initRealm()
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

    fun autoOpenDevTools(context: Context) = RabbitSettings.autoOpenDevTools(context)

    fun destroy() {
        RabbitHttpLogStorageManager.destroy()
        RabbitExceptionLogStorageManager.destroy()
    }

    private fun initRealm(){
        Realm.init(application)
        val realmConfig = RealmConfiguration.Builder().name("rabbit").deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(realmConfig)
    }


}