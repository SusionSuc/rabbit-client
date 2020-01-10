package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
class RabbitStorageConfig(
    @Transient val daoProvider: ArrayList<RabbitDaoProviderConfig> = ArrayList(),
    @Transient  val storageInOnSessionData: ArrayList<Class<Any>> = ArrayList(),
    var oneSessionValidDatas:ArrayList<String> = ArrayList() // 仅用在设置中展示
)