package com.susion.rabbit.storage

import android.app.Application
import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-12-12
 */
object RabbitStorage {

    fun init(application_: Application, config: Config) {
    }

    class Config(
        var daoProvider: ArrayList<RabbitDaoPluginProvider> = ArrayList(),
        var storageInOnSessionData: List<Class<Any>> = ArrayList()
    )

}

class RabbitDaoPluginProvider(val clazz: Class<Any>, val dao: AbstractDao<Any, Long>)