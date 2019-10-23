package com.susion.rabbit.net

import com.susion.rabbit.storage.RabbitDbStorageManager

/**
 * susionwang at 2019-09-24
 */
object RabbitHttpLogStorageManager : RabbitDbStorageManager() {

    private val TAG = javaClass.simpleName

    override fun getMaxDataNumber() = 100

    override fun getDbTimeField() = "time"

}