package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.common.toastInThread
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.storage.RabbitStorage

/**
 * susionwang at 2019-12-12
 *
 * 1. 通过 [Thread.setDefaultUncaughtExceptionHandler] 捕获Java层异常
 *
 */
internal class RabbitExceptionMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol {

    override fun open(context: Context) {
        val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            saveCrash(throwable, thread, defaultExceptionHandler)
        }
        isOpen = true
    }

    override fun close() {
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.EXCEPTION

    fun saveCrash(
        e: Throwable,
        thread: Thread,
        defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
    ) {
        if (!isOpen) return
        toastInThread("发生异常! 日志已保存", RabbitMonitor.application)
        Thread.sleep(1000) // 把toast给弹出来
        val exceptionInfo = translateThrowableToExceptionInfo(e, Thread.currentThread().name)
        RabbitStorage.saveSync(exceptionInfo)
        //main thead 下崩溃
        if (RabbitUtils.isMainThread(thread.id)) {
            Thread.sleep(1500)
            defaultExceptionHandler?.uncaughtException(thread, e)
        }
    }

    private fun translateThrowableToExceptionInfo(
        e: Throwable,
        currentThread: String
    ): RabbitExceptionInfo {
        val exceptionInfo = RabbitExceptionInfo()
        exceptionInfo.apply {
            crashTraceStr = RabbitLog.getStackTraceString(e)
            exceptionName = e.javaClass.name
            simpleMessage = e.message ?: ""
            threadName = currentThread
            time = System.currentTimeMillis()
            pageName = RabbitMonitor.getCurrentPage()
        }

        return exceptionInfo
    }

}