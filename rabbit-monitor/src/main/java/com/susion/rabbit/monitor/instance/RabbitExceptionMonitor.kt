package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.common.toastInThread
import com.susion.rabbit.entities.RabbitExceptionInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.RabbitMonitorProtocol
import com.susion.rabbit.storage.RabbitDbStorageManager
import java.io.PrintWriter
import java.io.StringWriter

/**
 * susionwang at 2019-12-12
 */
class RabbitExceptionMonitor : RabbitMonitorProtocol {

    override fun open(context: Context) {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrash(throwable, thread, defaultExceptionHandler)
        }
    }

    override fun close() {

    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.EXCEPTION

    override fun isOpen() = true

    fun saveCrash(
        e: Throwable,
        thread: Thread,
        defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    ) {
        toastInThread("发生异常! 日志已保存", RabbitMonitor.application)
        Thread.sleep(1000) // 把toast给弹出来
        val exceptionInfo = translateThrowableToExceptionInfo(e, Thread.currentThread().name)
        RabbitDbStorageManager.saveSync(exceptionInfo)
        defaultExceptionHandler?.uncaughtException(thread, e)
    }

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


}