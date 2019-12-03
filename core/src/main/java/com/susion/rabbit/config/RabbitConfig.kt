package com.susion.rabbit.config

import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.db.RabbitDaoPluginProvider

/**
 * susionwang at 2019-10-14
 * rabbit 配置类
 */
class RabbitConfig(
    //是否是Debug包
    var isInDebug: Boolean = false,
    //主页面自定义 ItemView
    var entryFeatures: List<RabbitMainFeatureInfo> = ArrayList(),
    //自定义数据库存储, support by green dao
    var daoProvider: List<RabbitDaoPluginProvider> = ArrayList(),
    //每次打开app时需要清除哪些数据
    var storageInOnSessionData: List<Class<Any>> = ArrayList(),
    //tracer config
    var monitorConfig: MonitorConfig = MonitorConfig()
) {

    class MonitorConfig(
        //卡顿栈采集周期
        var blockStackCollectPeriod: Long = STANDARD_FRAME_NS,
        //卡顿检测时间
        var blockThreshold: Long = STANDARD_FRAME_NS * 10,
        //自动打开页面测速功能
        var autoOpenPageSpeedMonitor: Boolean = false,
        //内存监控 -> 内存采样周期 default 3s
        var memoryValueCollectPeriod: Long = 3000L
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }
}