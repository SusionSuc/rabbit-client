package com.susion.rabbit.monitor.core

import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR

/**
 * susionwang at 2020-04-17
 * 重复利用 Choreographer Monitor
 */
internal object ChoreographerMonitorCenter {

    private val detailedFrameUpdateMonitor by lazy {
        LazyChoreographerFrameUpdateMonitor().apply {
            init()
        }
    }

    private val simpleFrameUpdateMonitor by lazy {
        ChoreographerFrameUpdateMonitor()
    }

    fun addSimpleFrameUpdateListener(listener: ChoreographerFrameUpdateMonitor.FrameUpdateListener) {
        if (simpleFrameUpdateMonitor.getCurrentListenerSize() == 0) {
            simpleFrameUpdateMonitor.startMonitorFrame()
            RabbitLog.d(TAG_MONITOR, "start simpleFrameUpdateMonitor")
        }
        simpleFrameUpdateMonitor.addFrameUpdateListener(listener)
    }

    fun removeSimpleFrameUpdateListener(listener: ChoreographerFrameUpdateMonitor.FrameUpdateListener) {
        simpleFrameUpdateMonitor.removeFrameUpdateListener(listener)
        if (simpleFrameUpdateMonitor.getCurrentListenerSize() == 0) {
            simpleFrameUpdateMonitor.stopMonitorFrame()
            RabbitLog.d(TAG_MONITOR, "stop simpleFrameUpdateMonitor")
        }
    }

    fun addDetailedFrameUpdateListener(listener: LazyChoreographerFrameUpdateMonitor.FrameUpdateListener) {
        if (detailedFrameUpdateMonitor.getCurrentListenerSize() == 0) {
            detailedFrameUpdateMonitor.startMonitor()
            RabbitLog.d(TAG_MONITOR, "start detailedFrameUpdateMonitor")
        }
        detailedFrameUpdateMonitor.addFrameUpdateListener(listener)
    }

    fun removeDetailedFrameUpdateListener(listener: LazyChoreographerFrameUpdateMonitor.FrameUpdateListener) {
        detailedFrameUpdateMonitor.removeFrameUpdateListener(listener)
        if (detailedFrameUpdateMonitor.getCurrentListenerSize() == 0) {
            detailedFrameUpdateMonitor.stopMonitor()
            RabbitLog.d(TAG_MONITOR, "stop detailedFrameUpdateMonitor")
        }
    }

}