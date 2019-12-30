package com.susion.rabbit

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.base.utils.FloatingViewPermissionHelper
import okhttp3.Interceptor
import okhttp3.Response

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

        Log.d("rabbit-noop", "config ... ")

        //base ui config
        val uiConfig = config.uiConfig
        RabbitUi.init(application, uiConfig)
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
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                return chain.proceed(chain.request())
            }

        }
    }

    fun saveCrashLog(it: Throwable?) {

    }

    fun isMainProcess(context: Context): Boolean {
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