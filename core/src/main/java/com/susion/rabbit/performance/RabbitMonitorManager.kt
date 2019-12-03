package com.susion.rabbit.performance

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.susion.rabbit.config.RabbitConfig
import com.susion.rabbit.config.RabbitSettings
import com.susion.rabbit.performance.core.RabbitMonitor
import com.susion.rabbit.performance.monitor.RabbitAppSpeedMonitor
import com.susion.rabbit.performance.monitor.RabbitBlockMonitor
import com.susion.rabbit.performance.monitor.RabbitFPSMonitor

/**
 * susionwang at 2019-10-18
 * 所有监控的管理者
 */
object RabbitMonitorManager {

    private val TAG = "rabbit-monitor"
    private var mContext: Application? = null
    private var initStatus = false
    private var mConfig: RabbitConfig.TraceConfig = RabbitConfig.TraceConfig()
    val monitorList = ArrayList<RabbitMonitor>().apply {
        add(RabbitAppSpeedMonitor())
        add(RabbitFPSMonitor())
        add(RabbitBlockMonitor())
    }

    private val appSpeedMonitor = RabbitAppSpeedMonitor()

    fun init(context: Application, config: RabbitConfig.TraceConfig) {
        if (!isMainProcess(context)) return
        if (initStatus) return

        mConfig = config
        mContext = context
        initStatus = true

        monitorList.forEach {
            if (RabbitSettings.autoOpen(context, it.getMonitorInfo().enName)) {
                it.open(context)
            }
        }
    }

    fun openMonitor(name: String) {
        assertInit()
        monitorList.forEach {
            if (it.getMonitorInfo().enName == name) {
                it.open(mContext!!)
            }
        }
    }

    fun closeMonitor(name: String) {
        assertInit()
        monitorList.forEach {
            if (it.getMonitorInfo().enName == name) {
                it.close()
            }
        }
    }

    private fun assertInit() {
        if (!initStatus) {
            throw RuntimeException("RabbitMonitorManager not call open!")
        }
    }

    fun pageSpeedMonitorIsOpen() = appSpeedMonitor.isOpen()

    fun markRequestFinish(requestUrl: String, costTime: Long = 0) {
        appSpeedMonitor.markRequestFinish(requestUrl, costTime)
    }

    fun monitorRequest(requestUrl: String) = appSpeedMonitor.monitorRequest(requestUrl)

    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(
            context
        )
    }

    private fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                processName = process.processName
            }
        }
        return processName
    }

}