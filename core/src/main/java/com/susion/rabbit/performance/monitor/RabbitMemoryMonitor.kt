package com.susion.rabbit.performance.monitor

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.performance.core.RabbitMonitor
import com.susion.rabbit.performance.entities.RabbitMemoryInfo
import com.susion.rabbit.ui.RabbitUiManager
import com.susion.rabbit.utils.RabbitUiUtils

/**
 * susionwang at 2019-12-03
 * 内存监控
 */
class RabbitMemoryMonitor : RabbitMonitor {

    private val TAG = javaClass.simpleName
    private var isOpen = false
    private var MEMORY_COLLECT_PERIOD = 10000L
    private var mActivityManager: ActivityManager? = null
    private var memoryRefreshHandler: Handler? = null
    private var mCtx: Context? = null
    private val memoryCollectRunnable = object : Runnable {
        override fun run() {
            val memInfo = getMemoryInfo()

            RabbitLog.d(TAG, "vm size : ${RabbitUiUtils.formatFileSize(memInfo.vmSize.toLong())}  native size : ${RabbitUiUtils.formatFileSize(memInfo.nativeSize.toLong())}")
            RabbitDbStorageManager.save(memInfo)
            Rabbit.uiManager.updateUiFromAsyncThread(
                RabbitUiManager.MSG_UPDATE_MEMORY_VALUE,
                "${RabbitUiUtils.formatFileSize(memInfo.totalSize.toLong())} "
            )
            memoryRefreshHandler?.postDelayed(this, MEMORY_COLLECT_PERIOD)
        }
    }

    private var monitorThread: HandlerThread? = null

    override fun open(context: Context) {
        mCtx = context
        MEMORY_COLLECT_PERIOD = Rabbit.geConfig().monitorConfig.memoryValueCollectPeriod
        mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        monitorThread = HandlerThread("rabbit_memory_monitor_thread")
        monitorThread?.start()
        memoryRefreshHandler = Handler(monitorThread?.looper)
        memoryRefreshHandler?.postDelayed(memoryCollectRunnable, MEMORY_COLLECT_PERIOD)
        isOpen = true
    }

    override fun close() {
        monitorThread?.quitSafely()
        monitorThread = null
        Rabbit.uiManager.updateUiFromAsyncThread(
            RabbitUiManager.MSG_UPDATE_MEMORY_VALUE,
            0
        )
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitor.MEMORY

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
            if (memInfo.size > 0) {
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

        return memInfo
    }

    override fun isOpen() = isOpen

}