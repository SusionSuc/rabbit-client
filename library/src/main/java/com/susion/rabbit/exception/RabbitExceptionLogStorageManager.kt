package com.susion.rabbit.exception

import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * susionwang at 2019-10-10
 * 异常日志
 */
object RabbitExceptionLogStorageManager : RabbitDbStorageManager() {

    private val TAG = javaClass.simpleName

    fun saveExceptionToLocal(e: Throwable) {
        val currentThreadName = Thread.currentThread().name
        val exceptionInfo = translateThrowableToExceptionInfo(e, currentThreadName)
        saveObjToLocalFile(exceptionInfo)
    }

    private fun translateThrowableToExceptionInfo(
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

    override fun getMaxDataNumber() = 100

    override fun getDbTimeField() = "time"

}