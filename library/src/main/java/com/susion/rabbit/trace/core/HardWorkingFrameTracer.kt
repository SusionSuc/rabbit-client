package com.susion.rabbit.trace.core

import android.view.Choreographer
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.trace.frame.RabbitBlockMonitor

class HardWorkingFrameTracer : ChoreographerFrameUpdateMonitor() {

    private var isStop = false
    private val commitCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (isStop) return
            RabbitLog.d("execute HardWorkingFrameTracer commitCallback : $frameTimeNanos")
            choreographer?.postFrameCallback(this)
        }
    }

    private val blockMonitor by lazy {
        RabbitBlockMonitor()
    }


    fun startMonitorFrame() {
        isStop = false
        addFrameUpdateListener(blockMonitor)
        startMonitorChoreographerDoFrame()
        choreographer?.postFrameCallback(commitCallback)
        addCallbackToCommitQueue(Runnable {
            endMonitorChoreographerDoFrame()
        })
    }

    fun stopMonitorFrame() {
        isStop = true
    }

}
