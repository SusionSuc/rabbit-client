package com.susion.rabbit.report

import android.app.Application

/**
 * susionwang at 2019-12-05
 * 数据上报
 */
object RabbitReport {


    class ReportConfig(
        var reportMonitorData: Boolean = false,
        var reportPath: String = "http://127.0.0.1:8000/apmdb/upload-log",
        var notReportDataFormat: HashSet<Class<*>> = HashSet()
    )


    fun init(app: Application, config: ReportConfig) {

    }


}