package com.susion.rabbit.exception.entities

import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.io.Serializable

/**
 * susionwang at 2019-10-10
 */
@RealmClass(name = "rabbit_exception")
open class RabbitExceptionInfo(
    var crashTraceStr: String = "",
    var exceptionName: String = "",
    var simpleMessage: String = "",
    var threadName: String = "",
    var currentSystemVersion: String = "",
    var time: Long = System.currentTimeMillis()
) : RealmObject(), Serializable {

    fun isvalid(): Boolean {
        return true
    }
}