package com.susion.rabbit.monitor.instance

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.base.ui.RabbitUiEvent
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.storage.RabbitStorage

/**
 * susionwang at 2019-12-03
 * 内存监控
 */
internal class RabbitMemoryMonitor(override var isOpen: Boolean = false) :
    RabbitMonitorProtocol {

    private var MEMORY_COLLECT_PERIOD = 10000L
    private var mActivityManager: ActivityManager? = null
    private var memoryRefreshHandler: Handler? = null
    private var mCtx: Context? = null
    private val memoryCollectRunnable = object : Runnable {
        override fun run() {
            val memInfo = getMemoryInfo()
            RabbitStorage.save(memInfo)
            val eventType = RabbitUiEvent.MSG_UPDATE_MEMORY_VALUE
            val memoryStr = "${RabbitUiUtils.formatFileSize(memInfo.totalSize.toLong())} "
            RabbitMonitor.uiEventListener?.updateUi(eventType, memoryStr)
            memoryRefreshHandler?.postDelayed(this, MEMORY_COLLECT_PERIOD)
        }
    }

    private var monitorThread: HandlerThread? = null

    override fun open(context: Context) {
        mCtx = context
        MEMORY_COLLECT_PERIOD = RabbitMonitor.mConfig.memoryValueCollectPeriodMs
        mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        monitorThread = HandlerThread("rabbit_memory_monitor_thread")
        monitorThread?.start()
        memoryRefreshHandler = Handler(monitorThread?.looper)
        memoryRefreshHandler?.postDelayed(memoryCollectRunnable, MEMORY_COLLECT_PERIOD)
        isOpen = true
        RabbitLog.d(TAG_MONITOR, "max memory : ${RabbitUiUtils.formatFileSize(Runtime.getRuntime().maxMemory())}")
    }

    override fun close() {
        monitorThread?.quitSafely()
        monitorThread = null
        RabbitMonitor.uiEventListener?.updateUi(
            RabbitUiEvent.MSG_UPDATE_MEMORY_VALUE,
            ""
        )
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.MEMORY

    private fun getMemoryInfo(): RabbitMemoryInfo {
//        return getMemoryByActivityManager()
        return getMemoryInfoInDebug()
    }

    /**
     *  Android P 中 1分钟以内都会得到相同的值 没什么用。。。。
     * */
    @Deprecated("")
    fun getMemoryByActivityManager(): Long {
        try {
            // 统计进程的内存信息 totalPss  这个API多次调用会得到相同的值。
            val memInfo =
                mActivityManager?.getProcessMemoryInfo(intArrayOf(Process.myPid())) ?: return 0
            if (memInfo.isNotEmpty()) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                return memInfo[0].totalPss * 1024L
            }
        } catch (e: Exception) {
        }
        return 0
    }

    /**
     * 只能用在debug model,
     * */
    private fun getMemoryInfoInDebug(): RabbitMemoryInfo {
        val info = Debug.MemoryInfo()
        Debug.getMemoryInfo(info)

        val memInfo = RabbitMemoryInfo()
        memInfo.totalSize = (info.totalPss) * 1024 // 这个值比profiler中的total大一些
        memInfo.vmSize = (info.dalvikPss) * 1024   // 这个值比profiler中的 java 内存值小一些, Doesn't include other Dalvik overhead
        memInfo.nativeSize = info.nativePss * 1024
        memInfo.othersSize = info.otherPss * 1024 + info.totalSwappablePss * 1024
        memInfo.time = System.currentTimeMillis()
        memInfo.pageName = RabbitMonitor.getCurrentPage()

        return memInfo
    }

}