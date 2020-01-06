package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
/**
 * @property enable 是否发送数据
 * @property reportPath 数据上报的地址
 * */
class RabbitReportConfig(
    var enable: Boolean = false,
    var reportPath: String = "http://127.0.0.1:8000/apmdb/upload-log",
    var notReportDataFormat: HashSet<Class<*>> = HashSet(),
    var fpsReportPeriodS: Long = 10,
    var dataReportListener: DataReportListener? = null //监听数据上报
) {
    interface DataReportListener {
        fun onPrepareReportData(data: Any, currentUseTime: Long = 0)
    }
}