package com.susion.rabbit.demo

import android.app.Application
import android.content.Context
import android.util.Log
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitConfig
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.demo.page.CustomBusinessPage
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.ui.base.RabbitMainFeatureInfo
import com.susion.rabbit.ui.base.RabbitUi

/**
 * susionwang at 2019-12-12
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val rabbitConfig = RabbitConfig()

        rabbitConfig.isDebug = true
        rabbitConfig.monitorConfig = RabbitMonitor.Config().apply {
            autoOpenMonitors =
                hashSetOf(RabbitMonitorProtocol.NET.name, RabbitMonitorProtocol.EXCEPTION.name)
        }
        rabbitConfig.reportConfig = getReportConfig()

        val uiConfig = RabbitUi.Config()
        uiConfig.entryFeatures = arrayListOf<RabbitMainFeatureInfo>(
            RabbitMainFeatureInfo(
                "业务面板",
                R.mipmap.ic_launcher,
                CustomBusinessPage::class.java
            )
        )

        rabbitConfig.uiConfig = uiConfig

        Log.d("rabbit-noop", "MyApplication onCreate ")

        Rabbit.config(rabbitConfig)
    }

    private fun getReportConfig(): RabbitReport.ReportConfig {
        val config = RabbitReport.ReportConfig()
        return config
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Log.d("attachBaseContext", "called !!!")
    }

}