package com.susion.rabbit.trace.core

import android.view.Choreographer

/**
 * 向 Choreographer pos callback。 这个操作会导致应用不断接收 [Vsync 信号]
 * */
open class ChoreographerFrameUpdateMonitor {

    private var isStrt = false
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isStrt) return
            if (mLastFrameTimeNanos != 0L) {
                val diffFrameCoast = (frameTimeNanos - mLastFrameTimeNanos)
                mFrameListeners.forEach {
                    it.doFrame(diffFrameCoast)
                }
            }
            mLastFrameTimeNanos = frameTimeNanos
            Choreographer.getInstance().postFrameCallback(this)
        }
    }
    private val mFrameListeners = ArrayList<FrameUpdateListener>()
    private var mLastFrameTimeNanos: Long = 0

    fun startMonitorFrame() {
        isStrt = true
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stopMonitorFrame() {
        isStrt = false
        Choreographer.getInstance().removeFrameCallback(frameCallback)
    }

    interface FrameUpdateListener {
        fun doFrame(frameCostNs: Long)
    }

    fun addFrameUpdateListener(listener: FrameUpdateListener) {
        mFrameListeners.add(listener)
    }

    fun removeFrameUpdateListener(listener: FrameUpdateListener) {
        mFrameListeners.remove(listener)
    }

}
