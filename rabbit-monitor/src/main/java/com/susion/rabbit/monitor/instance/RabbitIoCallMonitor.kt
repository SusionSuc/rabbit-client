package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitIoCallInfo
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.tracer.RabbitScanIoOpHelper

/**
 * susionwang at 2020-01-08
 */
internal class RabbitIoCallMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol {

    override fun open(context: Context) {
        RabbitStorage.clear(RabbitIoCallInfo::class.java)
        RabbitAsync.asyncRun({
            val ioCallList = RabbitScanIoOpHelper.loadIoCallToDb()
            RabbitLog.d(TAG_MONITOR, "success load io call : ${ioCallList.size}")
            ioCallList.forEach {
                val ioCallInfo = RabbitIoCallInfo().apply {
                    invokeStr = it.split("&").firstOrNull() ?: ""
                    becalledStr = it.split("&").lastOrNull() ?: ""
                    time = System.currentTimeMillis()
                }
                RabbitStorage.save(ioCallInfo)
            }
        })
    }

    override fun close() {

    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.BLOCK_CALL

}