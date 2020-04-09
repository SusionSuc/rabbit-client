package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-03
 */
class RabbitStorageConfig(
    //配置Rabbit存储的数据， 基于green dao
    @Transient val daoProvider: ArrayList<RabbitDaoProviderConfig> = ArrayList(),
    //在一次应用打开期间存活的数据
    @Transient val storageInOneSessionData: ArrayList<String> = ArrayList(),
    var dataMaxSaveCountLimit: HashMap<String, Int> = HashMap(), // 各个类型数据量限制
    var oneSessionValidDatas: ArrayList<String> = ArrayList() // 仅用在设置中展示
)