package com.susion.rabbit.ui.global.entities

/**
 * create by susionwang at 2020-02-10
 *
 * 一个页面的全局监控信息
 */
class RabbitPagePerformanceInfo(
    var pageName: String = "",
    var avgFps: Int = 0,
    var avgMem: Long = 0,
    var avgInlfateTime: Int = 0,
    var avgFullRenderTime: Int = 0,
    var blockCount: Int = 0,
    var slowMethodCount: Int = 0,

    //辅助计算
    var fpsCount: Int = 0,
    var memCount: Int = 0,
    var pageCount:Int = 0
)