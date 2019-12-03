package com.susion.rabbit.performance.monitor

import android.app.ActivityManager
import android.content.Context
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
import java.lang.Runtime.getRuntime

/**
 * susionwang at 2019-12-03
 * 内存监控
 */
class RabbitMemoryMonitor  {

    private val TAG = javaClass.simpleName
    private var isOpen = false
    private var MEMORY_COLLECT_PERIOD = 10000L
    private var mActivityManager: ActivityManager? = null
    private var memoryRefreshHandler: Handler? = null
    private var mCtx:Context? = null
    private val memoryCollectRunnable = object : Runnable {
        override fun run() {
            val currentMemoryValue = getCurrentMemorySize()
            RabbitLog.d(TAG, "memory info : ${RabbitUiUtils.formatFileSize(currentMemoryValue)}")
            val memoryInfo = RabbitMemoryInfo(System.currentTimeMillis(), currentMemoryValue.toInt())
            RabbitDbStorageManager.save(memoryInfo)
            Rabbit.uiManager.updateUiFromAsyncThread(
                RabbitUiManager.MSG_UPDATE_MEMORY_VALUE,
                currentMemoryValue
            )
            memoryRefreshHandler?.postDelayed(this, MEMORY_COLLECT_PERIOD)
        }
    }
    private var monitorThread: HandlerThread? = null

     fun open(context: Context) {
        mCtx = context
        MEMORY_COLLECT_PERIOD = Rabbit.geConfig().traceConfig.memoryValueCollectPeriod
        mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        monitorThread = HandlerThread("rabbit_memory_monitor_thread")
        monitorThread?.start()
        memoryRefreshHandler = Handler(monitorThread?.looper)
        memoryRefreshHandler?.postDelayed(memoryCollectRunnable, MEMORY_COLLECT_PERIOD)
        isOpen = true

        RabbitLog.d(
            TAG,
            "进程最大内存为 : ${RabbitUiUtils.formatFileSize(getRuntime().maxMemory())}"
        )
    }

    private fun getCurrentMemorySize(): Long {
        try {
            // 统计进程的内存信息 totalPss  这个API多次调用会得到相同的值。 pss:{1分钟以内都会得到相同的值}
            val memInfo = mActivityManager?.getProcessMemoryInfo(intArrayOf(Process.myPid())) ?: return 0
            if (memInfo.size > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                return memInfo[0].totalPss * 1024L
            }
        } catch (e: Exception) { }

        return 0

//        val memInfo = ActivityManager.MemoryInfo()
//        mActivityManager?.getMemoryInfo(memInfo)
//
////        val maxMemory = RabbitUiUtils.formatFileSize(getRuntime().maxMemory())
//        val maxMemory = RabbitUiUtils.formatFileSize(memInfo.totalMem)
//        val vmTotalMemory = RabbitUiUtils.formatFileSize(getRuntime().totalMemory())
//        val freeMemory =RabbitUiUtils.formatFileSize(getRuntime().freeMemory())
//
//        val allocatedMemory = RabbitUiUtils.formatFileSize(memInfo.totalMem - memInfo.availMem)
////        val allocatedMemory = RabbitUiUtils.formatFileSize(getRuntime().totalMemory() - getRuntime().freeMemory())
//        val pssMemory =RabbitUiUtils.formatFileSize(Debug.getPss() * 1024)
//
//        RabbitLog.d(TAG, "maxMemory : $maxMemory;  vmTotalMemory : $vmTotalMemory; freeMemory : $freeMemory; pssMemory : $pssMemory; allocatedMemory : $allocatedMemory")
//

    }

    fun stopMonitor() {
        monitorThread?.quitSafely()
        monitorThread = null
        isOpen = false
        Rabbit.uiManager.updateUiFromAsyncThread(
            RabbitUiManager.MSG_UPDATE_MEMORY_VALUE,
            0
        )
    }

    fun isOpen() = isOpen

}