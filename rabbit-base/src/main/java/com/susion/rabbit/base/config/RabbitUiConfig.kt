package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
class RabbitUiConfig(
    val entryFeatures: ArrayList<RabbitMainFeatureInfo> = ArrayList(),
    val customConfigList: ArrayList<RabbitCustomConfigProtocol> = ArrayList()
)