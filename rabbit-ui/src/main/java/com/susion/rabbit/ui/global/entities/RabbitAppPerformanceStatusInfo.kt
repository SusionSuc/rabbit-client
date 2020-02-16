package com.susion.rabbit.ui.global.entities

import com.susion.rabbit.base.entities.RabbitAppPerformanceInfo

/**
 * create by susionwang at 2020-02-16
 * 性能监控占坑的信息，懒加载
 */
class RabbitAppPerformancePitInfo(
    var recordStartTime: String = "",
    var duration: Long = 0,
    var isRunning: Boolean = false,
    var globalMonitorInfo: RabbitAppPerformanceInfo
)