package com.susion.rabbit.db

import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-10-28
 * 插件扩展数据存储  -> [com.susion.rabbit.config.RabbitConfig]
 */
class RabbitDaoPluginProvider(val clazz: Class<Any>, val dao:AbstractDao<Any, Long>)