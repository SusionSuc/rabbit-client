package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 * rabbit 配置类
 */
class RabbitConfig(
    var enable: Boolean = true,
    var enableLog: Boolean = true,
    @Transient var uiConfig: RabbitUiConfig = RabbitUiConfig(),
    var storageConfig: RabbitStorageConfig = RabbitStorageConfig(),
    var monitorConfig: RabbitMonitorConfig = RabbitMonitorConfig(),
    var reportConfig: RabbitReportConfig = RabbitReportConfig()
)