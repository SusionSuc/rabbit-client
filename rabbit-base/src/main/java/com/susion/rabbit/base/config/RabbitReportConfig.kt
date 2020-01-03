package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
/**
 * @property reportMonitorData 是否发送数据
 * @property reportPath 数据上报的地址
 * */
class RabbitReportConfig(
    var reportMonitorData: Boolean = false,
    var reportPath: String = "http://127.0.0.1:8000/apmdb/upload-log",
    var notReportDataFormat: HashSet<Class<*>> = HashSet(),
    var fpsReportPeriodS: Long = 10
)