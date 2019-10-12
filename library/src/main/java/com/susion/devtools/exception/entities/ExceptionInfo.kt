package com.susion.devtools.exception.entities

import com.susion.devtools.base.DevToolsBaseInfo
import java.io.Serializable

/**
 * susionwang at 2019-10-10
 */
class ExceptionInfo(
    var crashTraceStr: String = "",
    var exceptionName: String = "",
    var simpleMessage: String = "",
    var filePath: String = "",
    var threadName: String = "",
    val currentSystemVersion: String = ""
) : DevToolsBaseInfo(), Serializable {
    fun isValid(): Boolean {
        return true
    }
}