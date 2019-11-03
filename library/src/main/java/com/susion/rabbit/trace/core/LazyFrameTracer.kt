package com.susion.rabbit.trace.core

import com.susion.rabbit.trace.frame.RabbitFPSMonitor

/**
 * susionwang at 2019-10-18
 *
 * NOTE: 只有在主线程消息循环的时候才监控帧率
 *
 */
internal class LazyFrameTracer(private val uiThreadLooperMonitor: UIThreadLooperMonitor) :
    ChoreographerFrameUpdateMonitor() {

    private var fpsMonitorIsOpen = false
    private val fpsMonitor by lazy {
        RabbitFPSMonitor()
    }

    private val looperListener = object :
        UIThreadLooperMonitor.LooperHandleEventListener {
        override fun onMessageLooperStartHandleMessage() {
            startMonitorChoreographerDoFrame()
        }

        override fun onMessageLooperStopHandleMessage() {
            endMonitorChoreographerDoFrame()
        }
    }

    private fun monitorUiThreadLoop() {
        uiThreadLooperMonitor.addLooperHandleEventListener(looperListener)
    }

    private fun stopMonitorUiThreadLoop(){
        uiThreadLooperMonitor.removeLooperHandleEventListener(looperListener)
    }

    /**
     * 监控应用帧率
     * */
    fun startMonitorFps() {
        monitorUiThreadLoop()
        addFrameUpdateListener(fpsMonitor)
        fpsMonitorIsOpen = true
    }

    fun stopMonitorFps(){
        stopMonitorUiThreadLoop()
        removeFrameUpdateListener(fpsMonitor)
        fpsMonitorIsOpen = false
    }

    fun fpsMonitorIsOpen() = fpsMonitorIsOpen

}