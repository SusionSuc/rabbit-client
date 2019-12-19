package com.susion.rabbit

import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.report.RabbitReport
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.RabbitUi

/**
 * susionwang at 2019-10-14
 * rabbit 配置类
 */
class RabbitConfig(
    var isDebug: Boolean = true,
    var enableLog: Boolean = true,
    var uiConfig: RabbitUi.Config = RabbitUi.Config(),
    var storageConfig: RabbitStorage.Config = RabbitStorage.Config(),
    var monitorConfig: RabbitMonitor.Config = RabbitMonitor.Config(),
    var reportConfig: RabbitReport.ReportConfig = RabbitReport.ReportConfig()
)