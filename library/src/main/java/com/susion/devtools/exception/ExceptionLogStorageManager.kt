package com.susion.devtools.exception

import com.susion.devtools.base.DevToolsFileStorageManager
import com.susion.devtools.exception.entities.ExceptionInfo
import com.susion.devtools.utils.runOnIoThread
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

/**
 * susionwang at 2019-10-10
 * 异常日志
 */
object ExceptionLogStorageManager : DevToolsFileStorageManager() {

    private val TAG = javaClass.simpleName
    private val disposableList = ArrayList<Disposable>()

    fun saveExceptionToLocal(e: Throwable) {
        val currentThreadName = Thread.currentThread().name
        val dis = runOnIoThread {
            val exceptionInfo = translateThrowableToExceptionInfo(e, currentThreadName)
            saveObjToLocalFile(exceptionInfo, TYPE_EXCEPTION)
        }
        disposableList.add(dis)
    }

    fun getAllExceptionFiles(loadResult: (exceptionList: List<ExceptionInfo>) -> Unit) {
        val dis = getAllExceptionFiles(TYPE_EXCEPTION, ExceptionInfo::class.java, loadResult)
        disposableList.add(dis)
    }

    private fun translateThrowableToExceptionInfo(
        e: Throwable,
        currentThread: String
    ): ExceptionInfo {
        val exceptionInfo = ExceptionInfo()
        val strWriter = StringWriter()
        e.printStackTrace(PrintWriter(strWriter))
        exceptionInfo.crashTraceStr = strWriter.buffer.toString()
        exceptionInfo.exceptionName = e.javaClass.name
        exceptionInfo.simpleMessage = e.message ?: ""
        exceptionInfo.filePath = getFilePath(TYPE_EXCEPTION)
        exceptionInfo.threadName = currentThread
        return exceptionInfo
    }

    fun destroy() {
        disposableList.forEach {
            it.dispose()
        }
    }
}