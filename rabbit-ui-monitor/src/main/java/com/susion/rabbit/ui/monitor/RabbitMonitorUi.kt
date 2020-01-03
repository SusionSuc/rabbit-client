package com.susion.rabbit.ui.monitor

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.monitor.page.*

/**
 * susionwang at 2019-12-30
 */

object RabbitMonitorUi {

    var eventListener: EventListener? = null
    var config: Config = Config()

    class Config(
        var monitorList: List<RabbitMonitorProtocol> = ArrayList()
    )

    fun init(application: Application, config: Config) {
        this.config = config
    }

    interface EventListener {
        fun toggleMonitorStatus(monitor: RabbitMonitorProtocol, open: Boolean)
    }

    fun defaultSupportFeatures(): ArrayList<RabbitMainFeatureInfo> {
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
                    "网络日志",
                    R.drawable.rabbit_icon_http,
                    RabbitHttpLogListPage::class.java
                )
            )
            add(
                RabbitMainFeatureInfo(
                    "异常日志", R.drawable.rabbit_icon_exception_face,
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
                        "com.mihoyo.hyperion",
                        "leakcanary.internal.activity.LeakActivity"
                    )
                    RabbitUi.appCurrentActivity?.get()?.startActivity(leakIntent)
                    RabbitUi.hideAllPage()
                }
            )

            add(
                RabbitMainFeatureInfo(
                    "内存分析",
                    R.drawable.rabbit_icon_memory_compose,
                    RabbitMemoryComposePage::class.java
                )
            )

            add(
                RabbitMainFeatureInfo(
                    "慢函数",
                    R.drawable.rabbit_icon_slow_method,
                    RabbitSlowMethodListPage::class.java
                )
            )
        }
    }

}