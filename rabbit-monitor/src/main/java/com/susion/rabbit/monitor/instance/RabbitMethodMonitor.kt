package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.Looper
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.entities.RabbitSlowMethodInfo
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.tracer.RabbitTracerEventNotifier

/**
 * susionwang at 2020-01-02
 * 函数监控
 * 1. 慢函数监控
 */
internal class RabbitMethodMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private val T = javaClass.simpleName
    private val slowMethodThreshold = RabbitMonitor.mConfig.slowMethodPeriodMs

    private val methodCostListener = object : RabbitTracerEventNotifier.MethodCostEvent {
        override fun methodCost(methodStr: String, time: Long) {
            val monitorCurrentThread = if (RabbitMonitor.mConfig.onlyCheckMainThreadSlowMethod) {
                Thread.currentThread().name == Looper.getMainLooper().thread.name
            } else {
                true
            }
            if (time > slowMethodThreshold && monitorCurrentThread) {
                saveSlowMethod(methodStr, time)
            }
        }
    }

    override fun open(context: Context) {
        RabbitTracerEventNotifier.methodCostNotifier = methodCostListener
    }

    override fun close() {
        RabbitTracerEventNotifier.methodCostNotifier =
            RabbitTracerEventNotifier.FakeMethodCostListener()
    }

    private fun saveSlowMethod(methodStr: String, time: Long) {

        val fullClassName = methodStr.split("&").firstOrNull() ?: ""
        val methodName = methodStr.split("&").lastOrNull() ?: ""
        val classNameStartIndex = fullClassName.lastIndexOf(".")

        if (classNameStartIndex > 0) {

            val className =
                fullClassName.subSequence(classNameStartIndex + 1, fullClassName.length).toString()
            val pkgName = fullClassName.subSequence(0, classNameStartIndex).toString()
            RabbitLog.d(
                TAG_MONITOR,
                "slow method --> $pkgName -> $className -> $methodName -> $time ms"
            )

            val slowMethod = RabbitSlowMethodInfo().apply {
                this.pkgName = pkgName
                this.className = className
                this.methodName = methodName
                this.costTimeMs = time
                this.time = System.currentTimeMillis()
                this.pageName = RabbitMonitor.getCurrentPage()
                callStack = RabbitUtils.traceToString(3, Throwable().stackTrace, 15)
            }

            RabbitStorage.save(slowMethod)
        }
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.SLOW_METHOD

}