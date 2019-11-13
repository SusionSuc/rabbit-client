package com.susion.rabbit.tracer.core

import com.susion.rabbit.tracer.frame.RabbitFPSMonitor

/**
 * susionwang at 2019-10-18
 *
 * NOTE: 只有在主线程消息循环的时候才监控帧率
 */
internal class LazyFrameTracer : LazyChoreographerFrameUpdateMonitor() {

    private var fpsMonitorIsOpen = false
    private val fpsMonitor by lazy {
        RabbitFPSMonitor()
    }

    /**
     * 监控应用帧率
     * */
    fun startMonitorFps() {
        startMonitor()
        addFrameUpdateListener(fpsMonitor)
        fpsMonitorIsOpen = true
    }

    fun stopMonitorFps(){
        stopMonitor()
        removeFrameUpdateListener(fpsMonitor)
        fpsMonitorIsOpen = false
    }

    fun fpsMonitorIsOpen() = fpsMonitorIsOpen

}