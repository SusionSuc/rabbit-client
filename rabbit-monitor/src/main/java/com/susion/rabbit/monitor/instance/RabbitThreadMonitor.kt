package com.susion.rabbit.monitor.instance

import android.content.Context
import android.os.HandlerThread
import android.os.Message
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.jvmti.NativeCommunityHandler
import com.susion.rabbit.jvmti.RabbitJvmTi

/**
 * susionwang at 2020-04-24
 */
class RabbitThreadMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    private val TAG_SUFFIX = "thread"
    private val monitorThread by lazy {
        HandlerThread("rabbit_thread_monitor_thread")
    }
    private val monitorHandler by lazy {
        object : NativeCommunityHandler(monitorThread.looper) {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                RabbitLog.d(TAG_MONITOR, TAG_SUFFIX, "receive message !!")
            }
        }
    }

    override fun open(context: Context) {
        monitorThread.start()
        RabbitJvmTi.init(context, monitorHandler)

    }

    override fun close() {
        monitorThread.quitSafely()
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.THREAD

}