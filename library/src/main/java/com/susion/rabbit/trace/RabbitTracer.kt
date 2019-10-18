package com.susion.rabbit.trace

import com.susion.rabbit.trace.core.UIThreadLooperMonitor
import com.susion.rabbit.trace.frame.FrameTracer
import com.susion.rabbit.trace.frame.RabbitFPSMonitor

/**
 * susionwang at 2019-10-18
 *
 * 所有监控的管理者
 */
object RabbitTracer {

    //帧率监控相关
    private val frameTracer = FrameTracer()
    private val fpsMonitor = RabbitFPSMonitor()

    private var fpsMonitorEnable = false
    private var initStatus = false

    fun init(){
        if (initStatus)return
        initStatus = true
        UIThreadLooperMonitor.init()
        frameTracer.init()
    }

    fun enableFPSTracer() {
        enableFrameTracer()
        if (!frameTracer.containFrameUpdateListener(fpsMonitor)) {
            fpsMonitorEnable = true
            frameTracer.addFrameUpdateListener(fpsMonitor)
        }
    }

    fun disableFPSTracer() {
        fpsMonitorEnable = false
        frameTracer.removeFrameUpdateListener(fpsMonitor)
        tryStopUiThreadLooperMonitor()
    }

    private fun tryStopUiThreadLooperMonitor() {
        UIThreadLooperMonitor.removeLooperHandleEventListener(frameTracer)
        if (UIThreadLooperMonitor.listenerSize() > 1) return
        UIThreadLooperMonitor.setEnableStatus(false)
    }

    private fun enableFrameTracer() {
        if (!UIThreadLooperMonitor.isEnable()) {
            UIThreadLooperMonitor.setEnableStatus(true)
        }
        if (!UIThreadLooperMonitor.containEventListener(frameTracer)) {
            UIThreadLooperMonitor.addLooperHandleEventListener(frameTracer)
        }
    }

    fun isFPSTracerEnable() = fpsMonitorEnable

}