package com.susion.rabbit.exception

import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.utils.toastInThread
import java.io.PrintWriter
import java.io.StringWriter

/**
 * susionwang at 2019-11-04
 */
internal object RabbitExceptionManager {

    private fun translateThrowableToExceptionInfo(
        e: Throwable,
        currentThread: String
    ): RabbitExceptionInfo {
        val exceptionInfo = RabbitExceptionInfo()
        val strWriter = StringWriter()
        e.printStackTrace(PrintWriter(strWriter))
        exceptionInfo.apply {
            crashTraceStr = strWriter.buffer.toString()
            exceptionName = e.javaClass.name
            simpleMessage = e.message ?: ""
            threadName = currentThread
            time = System.currentTimeMillis()
        }

        return exceptionInfo
    }

    /**
     * 收集所有线程的崩溃信息
     * */
    fun openGlobalExceptionCollector() {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrash(throwable, thread, defaultExceptionHandler)
        }
    }

    fun saveCrash(
        e: Throwable,
        thread: Thread,
        defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    ) {
        toastInThread("发生异常! 日志已保存")
        Thread.sleep(1000) // 把toast给弹出来
        val exceptionInfo = translateThrowableToExceptionInfo(e, Thread.currentThread().name)
        RabbitDbStorageManager.saveSync(exceptionInfo)
        defaultExceptionHandler?.uncaughtException(thread, e)
    }

}