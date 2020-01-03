package com.susion.rabbit.demo

import android.app.Application
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.config.RabbitConfig
import com.susion.rabbit.demo.page.CustomBusinessPage
import com.susion.rabbit.base.config.RabbitMainFeatureInfo

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
        rabbitConfig.monitorConfig.autoOpenMonitors.addAll(
            hashSetOf(
                RabbitMonitorProtocol.NET.name,
                RabbitMonitorProtocol.EXCEPTION.name
            )
        )
//
//
//        rabbitConfig.monitorConfig.blockThresholdNs = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS)
//        rabbitConfig.monitorConfig.blockStackCollectPeriodNs = TimeUnit.NANOSECONDS.convert(10, TimeUnit.MILLISECONDS)
//
//
//        rabbitConfig.monitorConfig.fpsCollectThresholdNs = TimeUnit.NANOSECONDS.convert(10, TimeUnit.MILLISECONDS)
//        rabbitConfig.monitorConfig.memoryValueCollectPeriodMs = 2000L

        rabbitConfig.reportConfig.reportMonitorData = true
        rabbitConfig.reportConfig.reportPath = "http://127.0.0.1:8000/apmdb/upload-log"
//        rabbitConfig.reportConfig.fpsReportPeriodS = 2
//        rabbitConfig.reportConfig.notReportDataFormat.addAll(hashSetOf(RabbitExceptionInfo::class.java))

        Rabbit.config(rabbitConfig)

    }

}