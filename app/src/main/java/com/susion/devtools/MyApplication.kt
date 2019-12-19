package com.susion.devtools

import android.app.Application
import android.content.Context
import android.util.Log
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitConfig
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.report.RabbitReport

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

        Rabbit.init(rabbitConfig)
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