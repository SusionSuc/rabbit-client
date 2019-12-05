package com.susion.rabbit.config

import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.db.RabbitDaoPluginProvider

/**
 * susionwang at 2019-10-14
 * rabbit 配置类
 * @property isInDebug 当前的包是否是debug包
 * @property entryFeatures 自定义页面入口, 参考 [com.susion.rabbit.ui.page.RabbitEntryPage]
 * @property daoProvider 自定义数据库存储, support by green dao
 * @property storageInOnSessionData 每次打开app时需要清除哪些日志数据
 * @property monitorConfig  监控模块的配置
 */
class RabbitConfig(
    var isInDebug: Boolean = true,
    var enableLog: Boolean = true,
    var entryFeatures: List<RabbitMainFeatureInfo> = ArrayList(),
    var daoProvider: List<RabbitDaoPluginProvider> = ArrayList(),
    var storageInOnSessionData: List<Class<Any>> = ArrayList(),
    var monitorConfig: MonitorConfig = MonitorConfig(),
    var reportConfig: ReportConfig = ReportConfig()
) {

    /**
     * @property blockStackCollectPeriod 卡顿栈采集周期
     * @property blockThreshold  卡顿检测阈值, 即卡顿多长时间算一次卡顿
     * @property autoOpenMonitors 自动打开的监控功能, name 取自 [com.susion.rabbit.performance.core.RabbitMonitor]
     * for example : [com.susion.rabbit.performance.core.RabbitMonitor.BLOCK.enName]
     * @property memoryValueCollectPeriod 多长时间采集一次内存状态
     * */
    class MonitorConfig(
        var blockStackCollectPeriod: Long = STANDARD_FRAME_NS,
        var blockThreshold: Long = STANDARD_FRAME_NS * 10,
        var autoOpenMonitors: List<String> = ArrayList(),
        var memoryValueCollectPeriod: Long = 2000L
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }

    /**
     * @property reportMonitorData 是否发送数据
     * @property reportPath 数据上报的地址
     * */
    class ReportConfig(
        var reportMonitorData: Boolean = false,
        var reportPath: String = "http://www.susion-rabbit.com"
    )

}