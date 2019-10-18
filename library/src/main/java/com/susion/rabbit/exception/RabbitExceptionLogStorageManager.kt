package com.susion.rabbit.exception

import com.susion.rabbit.base.RabbitFileStorageManager
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
object RabbitExceptionLogStorageManager : RabbitFileStorageManager() {

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

    fun getAllExceptionFiles(loadResult: (exceptionList: List<RabbitExceptionInfo>) -> Unit) {
        val dis = getAllExceptionFiles(TYPE_EXCEPTION, RabbitExceptionInfo::class.java, loadResult)
        disposableList.add(dis)
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