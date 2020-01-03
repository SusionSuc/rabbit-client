package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
/**
 * @property blockStackCollectPeriodNs 卡顿栈采集周期
 * @property blockThresholdNs  卡顿检测阈值, 即卡顿多长时间算一次卡顿
 * @property autoOpenMonitors
 *      自动打开的监控功能, name 取自 [com.susion.rabbit.performance.core.RabbitMonitor]
 *      for example : [com.susion.rabbit.performance.core.RabbitMonitor.BLOCK.enName]
 * @property memoryValueCollectPeriodMs 多长时间采集一次内存状态
 * @property fpsCollectThresholdNs fps采集周期，即多长时间计算一次FPS
 * @property fpsReportPeriodS 上报FPS信息的周期, 用户与页面交互的累计时间。 10 还是挺长的 ！
 * */
class RabbitMonitorConfig(
    var blockStackCollectPeriodNs: Long = STANDARD_FRAME_NS,
    var blockThresholdNs: Long = STANDARD_FRAME_NS * 10,
    var autoOpenMonitors: HashSet<String> = HashSet(),
    var memoryValueCollectPeriodMs: Long = 2000L,
    var fpsCollectThresholdNs: Long = STANDARD_FRAME_NS * 10,
    var fpsReportPeriodS: Long = 10,
    var slowMethodPeriodMs:Long = 500
) {
    companion object {
        var STANDARD_FRAME_NS = 16666666L
    }
}