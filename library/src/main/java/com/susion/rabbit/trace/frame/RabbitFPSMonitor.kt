package com.susion.rabbit.trace.frame

import android.view.Choreographer
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.reflect.RabbitReflectHelper
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * susionwang at 2019-10-18
 * 监控 应用 FPS
 */
class RabbitFPSMonitor : FrameTracer.FrameUpdateListener{

    private val TAG = javaClass.simpleName

    //1帧时间的阈值
    private var frameIntervalMs: Long = 16
    private val UI_UPDATE_DURATION = 200
    private var totalFrameMs:Long = 0
    private var totalFrameNumber:Long = 0

    private var lastTotalFrameMs:Long = 0
    private var lastTotalFrameNumber:Long = 0

    fun init(){
        val frameIntervalNanos = RabbitReflectHelper.reflectField<Long>(Choreographer.getInstance(), "mFrameIntervalNanos") ?: 16666666
        frameIntervalMs = TimeUnit.MILLISECONDS.convert(frameIntervalNanos, TimeUnit.NANOSECONDS) + 1
    }

    override fun doFrame(startMs: Long, endMs: Long, inputCostNs: Long, animationCostNs: Long, traversalCostNs: Long) {

        val costUnitFrameNumber = ((endMs - startMs) / frameIntervalMs) + 1

//        RabbitLog.d(TAG, "costUnitFrameNumber : $costUnitFrameNumber")

//        RabbitLog.d(TAG, "one frame cost : ${endMs - startMs}")
        totalFrameMs += (costUnitFrameNumber * frameIntervalMs)
        totalFrameNumber += 1

        val duration = totalFrameMs - lastTotalFrameMs
        val collectFrame = totalFrameNumber - lastTotalFrameNumber

//        RabbitLog.d(TAG, "totalFrameMs : $totalFrameMs ; lastTotalFrameMs : $lastTotalFrameMs;  totalFrameNumber : $totalFrameNumber; lastTotalFrameNumber : $lastTotalFrameNumber")
//        RabbitLog.d(TAG, "duration : $duration ; collectFrame : $collectFrame ")

        if (duration >= UI_UPDATE_DURATION) {
            val temp = 1000f * collectFrame / duration
            val fps = min(60f, temp)
            if (fps < 60){
                RabbitLog.d(TAG, "fps : $fps")
            }
            lastTotalFrameMs = totalFrameMs
            lastTotalFrameNumber = totalFrameNumber

        }
    }

}