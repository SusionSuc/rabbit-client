package com.susion.rabbit.trace

import android.app.Application
import com.susion.rabbit.trace.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.trace.core.FrameTracer
import com.susion.rabbit.trace.core.LazyFrameTracer

/**
 * susionwang at 2019-10-18
 *
 * 所有监控的管理者
 *
 */
object RabbitTracer {

    private var mContext: Application? = null
    private var initStatus = false
    var standardFrameCostNs = 16666666L

    private val lazyFrameTracer by lazy {
        LazyFrameTracer().apply {
            init()
        }
    }

    //卡顿监控相关
    private val frameTracer by lazy {
        FrameTracer()
    }

    fun init(context: Application) {
        if (initStatus) return
        mContext = context
        initStatus = true
    }

    fun openFpsMonitor() {
        lazyFrameTracer.startMonitorFps()
    }

    fun closeFpsMonitor() {
        lazyFrameTracer.stopMonitorFps()
    }

    fun fpsMonitorIsOpen()= lazyFrameTracer.fpsMonitorIsOpen()

    fun blockMonitorIsOpen() = frameTracer.blockMonitorIsOpen()

    fun openBlockMonitor() {
        frameTracer.startBlockMonitor()
    }

    fun closeBlockMonitor() {
        frameTracer.stopBlockMonitor()
    }

}