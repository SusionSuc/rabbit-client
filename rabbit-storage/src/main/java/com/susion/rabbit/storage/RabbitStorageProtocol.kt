package com.susion.rabbit.storage

import com.susion.rabbit.base.entities.RabbitInfoProtocol

/**
 * susionwang at 2020-04-24
 */
interface RabbitStorageProtocol {

    fun save(obj: RabbitInfoProtocol)

    fun <T : RabbitInfoProtocol> query(clazz: Class<T>, id: Long): T?

    fun <T : Any> delete(clazz: Class<T>, id: Long)

    fun <T : Any> clear(clazz: Class<T>)

    fun <T : Any> count(clazz: Class<T>): Long

    fun <T : RabbitInfoProtocol> update(
        clazz: Class<T>,
        obj: RabbitInfoProtocol,
        id: Long
    )

    fun <T : RabbitInfoProtocol> distinct(clazz: Class<T>, columnName: String): List<String>

}