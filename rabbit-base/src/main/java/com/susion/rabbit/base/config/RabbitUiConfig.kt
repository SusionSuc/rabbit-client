package com.susion.rabbit.base.config

import com.susion.rabbit.base.RabbitMonitorProtocol

/**
 * susionwang at 2020-01-03
 */
class RabbitUiConfig(
    val entryFeatures: ArrayList<RabbitMainFeatureInfo> = ArrayList(),
    val customConfigList: ArrayList<RabbitCustomConfigProtocol> = ArrayList(),
    var monitorList: List<RabbitMonitorProtocol> = ArrayList()
)