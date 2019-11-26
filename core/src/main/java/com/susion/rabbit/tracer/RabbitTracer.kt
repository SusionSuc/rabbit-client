package com.susion.rabbit.tracer

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.config.RabbitConfig
import com.susion.rabbit.config.RabbitSettings
import com.susion.rabbit.tracer.core.FrameTracer
import com.susion.rabbit.tracer.core.LazyFrameTracer
import com.susion.rabbit.tracer.monitor.RabbitAppSpeedMonitor

/**
 * susionwang at 2019-10-18
 *
 * 所有监控的管理者
 *
 */
object RabbitTracer {

    private val TAG = "rabbit-tracer"
    private var mContext: Application? = null
    private var initStatus = false
    private var mConfig: RabbitConfig.TraceConfig = RabbitConfig.TraceConfig()

    private val lazyFrameTracer by lazy {
        LazyFrameTracer().apply {
            init()
        }
    }

    private val frameTracer by lazy {
        FrameTracer()
    }

    private val appSpeedMonitor = RabbitAppSpeedMonitor()

    fun init(context: Application, config: RabbitConfig.TraceConfig) {
        if (!isMainProcess(context)) return
        if (initStatus) return

        mConfig = config
        mContext = context
        initStatus = true

        if (RabbitSettings.blockCheckAutoOpen(context)) {
            RabbitLog.d(TAG, "openBlockMonitor ")
            openBlockMonitor()
        }

        if (RabbitSettings.fpsCheckAutoOpenFlag(context)) {
            RabbitLog.d(TAG, "openFpsMonitor ")
            openFpsMonitor()
        }

        appSpeedMonitor.init(context)
        if (RabbitSettings.acSpeedMonitorOpenFlag(context) || mConfig.autoOpenPageSpeedMonitor) {
            RabbitLog.d(TAG, "openPageSpeedMonitor ")
            openPageSpeedMonitor()
        }

    }

    fun openFpsMonitor() {
        lazyFrameTracer.startMonitorFps()
    }

    fun closeFpsMonitor() {
        lazyFrameTracer.stopMonitorFps()
    }

    fun openBlockMonitor() {
        frameTracer.startBlockMonitor()
    }

    fun closeBlockMonitor() {
        frameTracer.stopBlockMonitor()
    }

    fun openPageSpeedMonitor() {
        appSpeedMonitor.startMonitorPageSpeed()
    }

    fun closePageSpeedMonitor() {
        appSpeedMonitor.stopMonitorPageSpeed()
    }

    fun fpsMonitorIsOpen() = lazyFrameTracer.fpsMonitorIsOpen()

    fun blockMonitorIsOpen() = frameTracer.blockMonitorIsOpen()

    fun pageSpeedMonitorIsOpen() = appSpeedMonitor.isOpen()

    fun markRequestFinish(requestUrl: String, costTime: Long = 0) {
        appSpeedMonitor.markRequestFinish(requestUrl, costTime)
    }

    fun monitorRequest(requestUrl: String) = appSpeedMonitor.monitorRequest(requestUrl)

    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(context)
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