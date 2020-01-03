package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
class RabbitStorageConfig (
    var daoProvider: ArrayList<RabbitDaoProviderConfig> = ArrayList(),
    var storageInOnSessionData: ArrayList<Class<Any>> = ArrayList()
)