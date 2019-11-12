package com.susion.rabbit.trace.frame

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.trace.RabbitExecuteManager
import com.susion.rabbit.trace.RabbitTracer
import com.susion.rabbit.trace.core.ChoreographerFrameUpdateMonitor
import com.susion.rabbit.trace.core.LazyChoreographerFrameUpdateMonitor
import com.susion.rabbit.trace.entities.RabbitBlockFrameInfo
import com.susion.rabbit.trace.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.utils.toastInThread
import java.util.concurrent.TimeUnit

/**
 * 监控应用卡顿
 *
 * */
class RabbitBlockMonitor : ChoreographerFrameUpdateMonitor.FrameUpdateListener {

    private var stackCollectHandler: Handler? = null
    private var blockStackTraces = HashMap<String, RabbitBlockStackTraceInfo>()

    private val stackCollectPeriod =  TimeUnit.MILLISECONDS.convert(Rabbit.geConfig().traceConfig.blockStackCollectPeriod, TimeUnit.NANOSECONDS)  // 栈采集周期
    private val blockThreshold = Rabbit.geConfig().traceConfig.blockThreshold

    //采集一次主线程堆栈, 【随机的， 因此并不一定是卡顿点, 不过抓住卡顿点的概率很大】
    private val blockStackCollectTask = object : Runnable {
        override fun run() {
            val stackTrace =  RabbitBlockStackTraceInfo(getUiThreadStackTrace())
            val mapKey = stackTrace.getMapKey()
            val info = blockStackTraces[mapKey]
            if (info == null){
                blockStackTraces[mapKey] = stackTrace
            }else{
                info.collectCount++
            }
            stackCollectHandler?.postDelayed(this,stackCollectPeriod)
        }
    }

    init {
        val sampleThread = HandlerThread("rabbit_block_monitor")
        sampleThread.start()
        stackCollectHandler = Handler(sampleThread.looper)
    }

    override fun doFrame(frameCostNs: Long) {
        stackCollectHandler?.removeCallbacks(blockStackCollectTask)
        if (frameCostNs > blockThreshold && blockStackTraces.isNotEmpty()) {
            toastInThread("检测到卡顿!!")
            val blockFrameInfo = RabbitBlockFrameInfo()
            blockFrameInfo.apply {
                costTime = frameCostNs
                blockFrameStrackTraceStrList = Gson().toJson(blockStackTraces.values.toList())
                blockIdentifier = getIdentifierByMaxCount(blockStackTraces)
                time = System.currentTimeMillis()
            }
            RabbitExecuteManager.DB_THREAD.execute {
                RabbitDbStorageManager.save(blockFrameInfo)
            }
        }

        stackCollectHandler?.postDelayed(blockStackCollectTask, stackCollectPeriod)

        blockStackTraces.clear()
    }

    private fun getUiThreadStackTrace(): String {
        return traceToString(0, Looper.getMainLooper().thread.stackTrace)
    }

    private fun traceToString(skipStackCount: Int, stackArray: Array<StackTraceElement>): String {
        if (stackArray.isEmpty()) {
            return "[]"
        }

        val b = StringBuilder()
        for (i in 0 until stackArray.size - skipStackCount) {
            if (i == stackArray.size - skipStackCount - 1) {
                return b.toString()
            }
            b.append(stackArray[i])
            b.append("\n")
        }
        return b.toString()
    }

    private fun getIdentifierByMaxCount(traceMap:Map<String, RabbitBlockStackTraceInfo>):String{
        return traceMap.values.toList().maxBy { it.collectCount }?.stackTrace.toString()
    }

}