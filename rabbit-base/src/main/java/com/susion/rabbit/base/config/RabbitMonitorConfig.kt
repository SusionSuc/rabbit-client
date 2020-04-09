package com.susion.rabbit.base.config

import com.susion.rabbit.base.entities.RabbitAppSpeedMonitorConfig
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2020-01-03
 *
 * @property blockStackCollectPeriodNs 卡顿栈采集周期
 * @property blockThresholdNs  卡顿检测阈值, 即卡顿多长时间算一次卡顿
 * @property autoOpenMonitors 启动应用后自动打开哪些监控
 * @property memoryValueCollectPeriodMs 多长时间采集一次内存状态
 * @property fpsCollectThresholdNs fps采集周期，即多长时间计算一次FPS
 * @property fpsReportPeriodS 上报FPS信息的周期, 用户与页面交互的累计时间。 10 还是挺长的 ！
 * */
class RabbitMonitorConfig(
    val autoOpenMonitors: HashSet<String> = HashSet(),
    var memoryValueCollectPeriodMs: Long = 2000L,
    var monitorSpeedList: RabbitAppSpeedMonitorConfig = RabbitAppSpeedMonitorConfig(),
    //卡顿
    var blockStackCollectPeriodNs: Long = STANDARD_FRAME_NS,
    var blockThresholdNs: Long = STANDARD_FRAME_NS * 10,
    //slow method
    var slowMethodPeriodMs: Long = 50,
    var onlyCheckMainThreadSlowMethod: Boolean = true,
    //fps
    var fpsCollectThresholdNs: Long = STANDARD_FRAME_NS * 20,
    var fpsReportPeriodS: Long = 1,
    var fpsMonitorPkgList: ArrayList<String> = ArrayList(),
    //anr
    var anrCheckPeriodNs: Long = TimeUnit.NANOSECONDS.convert(5, TimeUnit.SECONDS),
    var anrStackCollectPeriodNs: Long = STANDARD_FRAME_NS
) {
    companion object {
        var STANDARD_FRAME_NS = 16666666L
    }
}