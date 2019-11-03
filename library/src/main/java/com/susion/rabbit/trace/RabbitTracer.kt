package com.susion.rabbit.trace

import android.app.Application
import com.susion.rabbit.trace.core.HardWorkingFrameTracer
import com.susion.rabbit.trace.core.UIThreadLooperMonitor
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

    //主线程消息循环
    private val mainThreadLooperMonitor = UIThreadLooperMonitor()

    private val lazyFrameTracer by lazy {
        LazyFrameTracer(mainThreadLooperMonitor)
    }

    //卡顿监控相关
    private val hardWorkingFrameTracer by lazy {
        HardWorkingFrameTracer()
    }

    fun init(context: Application) {
        if (initStatus) return
        mContext = context
        initStatus = true
        mainThreadLooperMonitor.init()
    }

    fun openFpsMonitor() {
        mainThreadLooperMonitor.enable = true
        lazyFrameTracer.startMonitorFps()
        enableMainThreadLooperMonitor()
    }

    fun closeFpsMonitor() {
        lazyFrameTracer.stopMonitorFps()
        closeMainThreadLooperMonitor()
    }

    fun fpsMonitorIsOpen()= lazyFrameTracer.fpsMonitorIsOpen()

    private fun closeMainThreadLooperMonitor() {
        mainThreadLooperMonitor.enable = mainThreadLooperMonitor.listenerSize() != 0
    }

    private fun enableMainThreadLooperMonitor() {
        if (!mainThreadLooperMonitor.enable) {
            mainThreadLooperMonitor.enable = true
        }
    }

}