package com.susion.rabbit.tracer.core

import com.susion.rabbit.tracer.frame.RabbitBlockMonitor

/**
 * susionwang at 2019-11-04
 */
class FrameTracer : ChoreographerFrameUpdateMonitor() {

    private var blockMonitorIsOpen = false
    private val blockMonitor by lazy {
        RabbitBlockMonitor()
    }

    fun startBlockMonitor() {
        blockMonitorIsOpen = true
        startMonitorFrame()
        addFrameUpdateListener(blockMonitor)
    }

    fun stopBlockMonitor() {
        blockMonitorIsOpen = false
        stopMonitorFrame()
        removeFrameUpdateListener(blockMonitor)
    }

    fun blockMonitorIsOpen() = blockMonitorIsOpen

}