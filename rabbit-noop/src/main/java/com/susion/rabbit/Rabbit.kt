package com.susion.rabbit

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import com.susion.rabbit.base.RabbitProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.page.RabbitEntryPage
import com.susion.rabbit.base.ui.utils.FloatingViewPermissionHelper
import com.susion.rabbit.storage.RabbitStorage
import okhttp3.Interceptor

/**
 * susionwang at 2019-09-23
 * dependencies in release
 *  直接使用这个类中的API来操控 Rabbit
 */
object Rabbit : RabbitProtocol {

    lateinit var application: Application

    override fun init(app: Application, config: RabbitConfig) {

        application = app

        try {
            if (!RabbitUtils.isMainProcess(application)) return
        } catch (e: Throwable) {
            return
        }

        //base ui init
        RabbitUiKernal.init(
            application,
            RabbitEntryPage(application, config.uiConfig.entryFeatures)
        )

        //存储配置
        RabbitStorage.init(application, config.storageConfig)
    }

    override fun reConfig(config: RabbitConfig) {

    }


    override fun open(requestPermission: Boolean, activity: Activity) {
        val overlayPermissionIsOpen = FloatingViewPermissionHelper.checkPermission(activity)

        if (!requestPermission && !overlayPermissionIsOpen) return

        if (overlayPermissionIsOpen) {
            RabbitUiKernal.showFloatingView()
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

    override fun openPage(pageClass: Class<out View>?, params: Any?) {
        RabbitUiKernal.openPage(pageClass, params)
    }

    override fun changeAutoOpenStatus(context: Context, autoOpen: Boolean) {
        RabbitSettings.autoOpenRabbit(context, autoOpen)
    }

    override fun isAutoOpen(context: Context) = RabbitSettings.autoOpenRabbit(context)

    override fun getNetInterceptor(): Interceptor {
        return Interceptor { chain -> chain.proceed(chain.request()) }
    }

    override fun saveCrashLog(it: Throwable) {
    }

    override fun getCurrentActivity() = RabbitUiKernal.appCurrentActivity?.get()

}