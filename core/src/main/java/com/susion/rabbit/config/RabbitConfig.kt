package com.susion.rabbit.config

import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.db.RabbitDaoPluginProvider
import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-10-14
 * rabbit 配置类
 */
class RabbitConfig(
    //主页面自定义 ItemView
    var entryFeatures: List<RabbitMainFeatureInfo> = ArrayList(),
    //自定义数据库存储, support by green dao
    var daoProvider: List<RabbitDaoPluginProvider> = ArrayList(),
    //每次打开app时需要清除哪些数据
    var storageInOnSessionData: List<Class<Any>> = ArrayList(),
    //tracer config
    var traceConfig: TraceConfig = TraceConfig()
) {

    class TraceConfig(
        //卡顿监控相关配置:
        //卡顿栈采集周期
        var blockStackCollectPeriod: Long = STANDARD_FRAME_NS,
        //卡顿检测时间
        var blockThreshold: Long = STANDARD_FRAME_NS * 10,
        var autoOpenPageSpeedMonitor:Boolean = false
    ) {
        companion object {
            var STANDARD_FRAME_NS = 16666666L
        }
    }

}