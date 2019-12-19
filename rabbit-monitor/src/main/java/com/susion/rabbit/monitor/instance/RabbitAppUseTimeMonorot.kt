package com.susion.rabbit.monitor.instance

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.susion.rabbit.base.RabbitMonitorProtocol
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-12-17
 * 用户使用时长监控
 */
internal class RabbitAppUseTimeMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol {

    private var totalTime = 0L
    private var oneSessionRunTime: Long = 0

    //应用可见性监控
    private val applicationLifecycle = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            oneSessionRunTime = System.currentTimeMillis()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            totalTime += (System.currentTimeMillis() - oneSessionRunTime)
        }
    }

    override fun open(context: Context) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(applicationLifecycle)
        oneSessionRunTime = System.currentTimeMillis()
        isOpen = true
    }

    override fun close() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(applicationLifecycle)
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.USE_TIME

    fun getAppUseTimeS(): Long {
        val useTime = totalTime + (System.currentTimeMillis() - oneSessionRunTime)
        return TimeUnit.SECONDS.convert(useTime, TimeUnit.MILLISECONDS)
    }

}