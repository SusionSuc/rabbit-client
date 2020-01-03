package com.susion.rabbit

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.base.utils.FloatingViewPermissionHelper
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 *   dependencies in release
 */
object Rabbit {

    lateinit var application: Application

    @JvmStatic
    fun config(config: RabbitConfig) {

        try {
            if (!isMainProcess(application)) return
        } catch (e: Throwable) {
            return
        }

        //base ui config
        val uiConfig = config.uiConfig
        RabbitUi.init(application, uiConfig)

        //存储配置
        RabbitStorage.init(application, config.storageConfig)
    }

    fun open(requestPermission: Boolean = true, activity: Activity) {
        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(activity)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            RabbitUi.showFloatingView()
        } else {
            FloatingViewPermissionHelper.showConfirmDialog(
                activity,
                object : FloatingViewPermissionHelper.OnConfirmResult {
                    override fun confirmResult(confirm: Boolean) {
                        if (confirm) {
                            FloatingViewPermissionHelper.tryStartFloatingWindowPermission(activity)
                        }
                    }
                })
        }
    }

    fun getNetInterceptor(): Interceptor {
        return Interceptor { chain -> chain.proceed(chain.request()) }
    }

    fun saveCrashLog(it: Throwable?) {

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

    fun autoOpen(context: Context) = RabbitSettings.autoOpenRabbit(context)

    fun enableAutoOpen(autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(application, autoOpen)
    }
}