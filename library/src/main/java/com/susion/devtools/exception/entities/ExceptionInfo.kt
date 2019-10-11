package com.susion.devtools.exception.entities

import java.io.Serializable

/**
 * susionwang at 2019-10-10
 */
class ExceptionInfo(
    var crashTraceStr: String = "",
    var exceptionName: String = "",
    var simpleMessage: String = "",
    var time: Long = System.currentTimeMillis(),
    var filePath: String = ""
) : Serializable {
    fun isValid(): Boolean {
        return true
    }
}