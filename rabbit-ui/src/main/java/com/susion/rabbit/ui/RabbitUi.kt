package com.susion.rabbit.ui

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.view.View
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.config.RabbitUiConfig
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.page.RabbitEntryPage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.*

/**
 * susionwang at 2019-12-30
 */
object RabbitUi {

    var eventListener: EventListener? = null
    var mConfig: RabbitUiConfig = RabbitUiConfig()

    fun init(application: Application, config: RabbitUiConfig) {
        mConfig = config
        RabbitUiKernal.init(
            application,
            RabbitEntryPage(application, mConfig.entryFeatures, rightOpeClickCallback = {
                RabbitUiKernal.openPage(RabbitQuickFunctionPage::class.java)
            })
        )
    }

    interface EventListener {
        fun toggleMonitorStatus(monitor: RabbitMonitorProtocol, open: Boolean)
    }

    fun defaultSupportFeatures(): ArrayList<RabbitMainFeatureInfo> {
        return ArrayList<RabbitMainFeatureInfo>().apply {
            add(
                RabbitMainFeatureInfo(
                    "功能配置",
                    R.drawable.rabbit_icon_feature_setting,
                    RabbitFunctionConfigPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "网络日志",
                    R.drawable.rabbit_icon_http,
                    RabbitHttpLogListPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "异常日志", R.drawable.rabbit_icon_exception,
                    RabbitExceptionListPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "卡顿日志",
                    R.drawable.rabbit_icon_block,
                    RabbitUiBlockListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "应用测速",
                    R.drawable.rabbit_icon_speed,
                    RabbitAppSpeedMonitorDetailPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "内存泄漏",
                    R.drawable.rabbit_icon_memory_leak,
                    null
                ) {
                    //隐私启动，显示调用会在release构建时有问题
                    val leakIntent = Intent()
                    leakIntent.component = ComponentName(
                        "com.susion.rabbit.demo",
                        "leakcanary.internal.activity.LeakActivity"
                    )
                    getCurrentActivity()?.startActivity(leakIntent)
                    hideAllPage()
                }
            )

            add(
                RabbitMainFeatureInfo(
                    "慢函数",
                    R.drawable.rabbit_icon_slow_method,
                    RabbitSlowMethodListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "阻塞调用列表",
                    R.drawable.rabbit_icon_io_call,
                    RabbitBlockCallListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "FPS分析",
                    R.drawable.rabbit_icon_fps,
                    RabbitFpsAnalyzerPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "内存分析",
                    R.drawable.rabbit_icon_memory_compose,
                    RabbitMemoryComposePage::class.java
                )
            )
        }
    }

    /**
     * delegate RabbitUiKernal
     * */
    fun openPage(pageClass: Class<out View>?, params: Any? = null) = RabbitUiKernal.openPage(pageClass, params)

    fun hideAllPage() = RabbitUiKernal.hideAllPage()

    fun getCurrentActivity() = RabbitUiKernal.appCurrentActivity?.get()

    fun updateUiFromAsyncThread(msgType: Int, params: Any)  = RabbitUiKernal.updateUiFromAsyncThread(msgType, params)

}