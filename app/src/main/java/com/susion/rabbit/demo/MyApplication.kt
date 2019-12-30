package com.susion.rabbit.demo

import android.app.Application
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitConfig
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.demo.page.CustomBusinessPage
import com.susion.rabbit.ui.base.RabbitMainFeatureInfo

/**
 * susionwang at 2019-12-12
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val rabbitConfig = RabbitConfig()

        // 自定义UI面板入口
        rabbitConfig.uiConfig.entryFeatures = arrayListOf(
            RabbitMainFeatureInfo(
                "业务面板",
                R.mipmap.ic_launcher,
                CustomBusinessPage::class.java
            )
        )

        //监控开关配置
        rabbitConfig.monitorConfig.autoOpenMonitors.addAll(hashSetOf(RabbitMonitorProtocol.NET.name, RabbitMonitorProtocol.EXCEPTION.name))

        Rabbit.config(rabbitConfig)

    }

}