package com.susion.rabbit.config

import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.db.RabbitDaoPluginProvider
import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-10-14
 */
class RabbitConfig(
    var entryFeatures: List<RabbitMainFeatureInfo> = ArrayList(),
    var daoProvider: List<RabbitDaoPluginProvider> = ArrayList(),
    var autoOpenRabbit: Boolean = true
)