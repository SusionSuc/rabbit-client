package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
class RabbitStorageConfig(
    val daoProvider: ArrayList<RabbitDaoProviderConfig> = ArrayList(),
    val storageInOnSessionData: ArrayList<Class<Any>> = ArrayList()
)