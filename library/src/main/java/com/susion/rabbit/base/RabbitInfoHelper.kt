package com.susion.rabbit.base

import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import java.io.PrintWriter
import java.io.StringWriter

/**
 * susionwang at 2019-10-10
 * bean 的转换出来
 */
object RabbitInfoHelper {

    fun translateThrowableToExceptionInfo(
        e: Throwable,
        currentThread: String
    ): RabbitExceptionInfo {
        val exceptionInfo = RabbitExceptionInfo()
        val strWriter = StringWriter()
        e.printStackTrace(PrintWriter(strWriter))
        exceptionInfo.crashTraceStr = strWriter.buffer.toString()
        exceptionInfo.exceptionName = e.javaClass.name
        exceptionInfo.simpleMessage = e.message ?: ""
        exceptionInfo.threadName = currentThread
        return exceptionInfo
    }
}