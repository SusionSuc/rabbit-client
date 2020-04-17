package com.susion.rabbit.monitor.core

import android.view.Choreographer

/**
 * 向 Choreographer post callback。 这个操作会导致应用不断接收 [Vsync 信号]
 * */
internal open class ChoreographerFrameUpdateMonitor {

    private var isStart = false
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isStart) return
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
        isStart = true
        Choreographer.getInstance().postFrameCallback(frameCallback)
    }

    fun stopMonitorFrame() {
        isStart = false
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

    fun getCurrentListenerSize() = mFrameListeners.size

}
