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
    var reportPath: String = UNDEFINE_REPORT_PATH,
    @Transient val notReportDataFormat: HashSet<Class<*>> = HashSet(),
    var batchReportPointCount: Int = 1, // 每次上报几个点
    var emitterSleepCount: Int = 1, // 点位不够时，发射器等待的次数
    var emitterFailedRetryCount: Int = 2, //点位发射失败重试次数
    var fpsReportPeriodS: Long = 10,
    var notReportDataNames: ArrayList<String> = ArrayList(), //仅在设置中显示
    @Transient var dataReportListener: DataReportListener? = null //监听数据上报
) {
    interface DataReportListener {

        /**
         * @return 返回false则终止这个数据的上报
         * */
        fun onPrepareReportData(data: Any, currentUseTime: Long = 0): Boolean
    }

    companion object {
        const val UNDEFINE_REPORT_PATH = "undefine"
    }

}