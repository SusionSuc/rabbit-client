package com.susion.rabbit.exception.entities

import com.susion.rabbit.base.RabbitFileBaseInfo
import java.io.Serializable

/**
 * susionwang at 2019-10-10
 */
class RabbitExceptionInfo(
    var crashTraceStr: String = "",
    var exceptionName: String = "",
    var simpleMessage: String = "",
    var filePath: String = "",
    var threadName: String = "",
    val currentSystemVersion: String = ""
) : RabbitFileBaseInfo(), Serializable {
    fun isValid(): Boolean {
        return true
    }
}