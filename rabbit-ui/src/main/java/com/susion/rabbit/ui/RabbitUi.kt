package com.susion.rabbit.ui

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.view.View
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.config.RabbitUiConfig
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.page.RabbitEntryPage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.*
import com.susion.rabbit.ui.global.RabbitPerformanceTestListPage
import com.susion.rabbit.ui.slowmethod.RabbitSlowMethodPreviewPage

/**
 * susionwang at 2019-12-30
 * UI层向外暴露的API
 * 顶层业务不要直接使用这个类，方法都委托给 ->[Rabbit.kt]
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
        fun getGlobalConfig(): RabbitConfig
        fun changeGlobalMonitorStatus(open: Boolean)
        fun closeAllMonitor()
    }

    fun defaultSupportFeatures(application: Application): ArrayList<RabbitMainFeatureInfo> {
        return ArrayList<RabbitMainFeatureInfo>().apply {
            add(
                RabbitMainFeatureInfo(
                    "监控配置",
                    R.drawable.rabbit_icon_feature_setting,
                    RabbitMonitorConfigPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "性能测试",
                    R.drawable.rabbit_icon_global_monitor,
                    RabbitPerformanceTestListPage::class.java
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
                    RabbitSpeedListPage::class.java
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
                        application.packageName,
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
                    RabbitSlowMethodPreviewPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "代码扫描",
                    R.drawable.rabbit_icon_io_call,
                    RabbitCodeScanPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "FPS分析",
                    R.drawable.rabbit_icon_fps,
                    RabbitFpsAnalyzerListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "内存分析",
                    R.drawable.rabbit_icon_memory_compose,
                    RabbitMemoryGlobalAnalyzerPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "ANR",
                    R.drawable.rabbit_icon_anr,
                    RabbitANRListPage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "线程分析",
                    R.drawable.rabbit_icon_thread,
                    RabbitANRListPage::class.java
                )
            )
        }
    }

    /**
     * delegate RabbitUiKernal
     * */
    fun openPage(pageClass: Class<out View>?, params: Any? = null) =
        RabbitUiKernal.openPage(pageClass, params)

    fun hideAllPage() = RabbitUiKernal.hideAllPage()

    fun getCurrentActivity() = RabbitUiKernal.appCurrentActivity?.get()

    fun refreshFloatingViewUi(msgType: Int, params: Any) =
        RabbitUiKernal.refreshFloatingViewUi(msgType, params)

}