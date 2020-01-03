package com.susion.rabbit.base.config

import org.greenrobot.greendao.AbstractDao

/**
 * susionwang at 2019-10-28
 * greendao 数据存储提供类
 */
class RabbitDaoProviderConfig(val clazz: Class<Any>, val dao: AbstractDao<Any, Long>)