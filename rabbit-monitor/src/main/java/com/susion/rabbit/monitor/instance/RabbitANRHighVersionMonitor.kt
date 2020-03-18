package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitMonitorProtocol

/**
 * susionwang at 2020-03-17
 * android 21 以上监控 ANR
 */
class RabbitANRHighVersionMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    override fun open(context: Context) {

    }

    override fun close() {
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.ANR

}