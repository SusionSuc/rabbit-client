package com.susion.rabbit.tracer

import android.app.Application
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.config.RabbitSettings
import com.susion.rabbit.tracer.core.FrameTracer
import com.susion.rabbit.tracer.core.LazyFrameTracer
import com.susion.rabbit.tracer.monitor.RabbitAppSpeedMonitor

/**
 * susionwang at 2019-10-18
 *
 * 所有监控的管理者
 *
 */
object RabbitTracer {

    private var mContext: Application? = null
    private var initStatus = false
    private val TAG = "rabbit-tracer"

    private val lazyFrameTracer by lazy {
        LazyFrameTracer().apply {
            init()
        }
    }

    private val frameTracer by lazy {
        FrameTracer()
    }

    private val appSpeedMonitor by lazy {
        RabbitAppSpeedMonitor()
    }

    fun init(context: Application) {
        if (initStatus) return
        mContext = context
        initStatus = true

        if (RabbitSettings.blockCheckAutoOpen(context)) {
            RabbitLog.d("openBlockMonitor ")
            openBlockMonitor()
        }

        if (RabbitSettings.fpsCheckAutoOpenFlag(context)) {
            RabbitLog.d("openFpsMonitor ")
            openFpsMonitor()
        }

        RabbitTracerEventNotifier.eventNotifier = object : RabbitTracerEventNotifier.TracerEvent {
            override fun applicationCreateCostTime(time: Long) {
                appSpeedMonitor.recordApplicationCreateCostTime(time)
            }
        }

    }

    fun openFpsMonitor() {
        lazyFrameTracer.startMonitorFps()
    }

    fun closeFpsMonitor() {
        lazyFrameTracer.stopMonitorFps()
    }

    fun fpsMonitorIsOpen() = lazyFrameTracer.fpsMonitorIsOpen()

    fun blockMonitorIsOpen() = frameTracer.blockMonitorIsOpen()

    fun openBlockMonitor() {
        frameTracer.startBlockMonitor()
    }

    fun closeBlockMonitor() {
        frameTracer.stopBlockMonitor()
    }

}