package com.susion.rabbit.report

import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_REPORT
import com.susion.rabbit.base.entities.RabbitReportInfo
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock

/**
 * susionwang at 2019-12-05
 * 数据发射工作
 */
internal class RabbitReportDataEmitterTask {

    companion object {
        val EMITER_QUEUE_MAX_SIZE = 100
    }

    private var currentSleepCount = 0
    private val MAX_SLEEP_COUNT = RabbitReport.mConfig.emitterSleepCount
    private var BATCH_EMITTER_NUMBER = RabbitReport.mConfig.batchReportPointCount
    private val MAX_EMITTER_FAILED_COUNT = RabbitReport.mConfig.emitterFailedRetryCount
    private val CIRCLE_EMITTER_TIME = 5000L
    private val trackPointsList = CopyOnWriteArrayList<RabbitReportInfo>()
    private val listModifierLock = ReentrantLock() //防止对 trackPointsList 的并发修改
    private val trackRequest = RabbitReportRequestManager()  //负责发射打点请求
    var eventListener: EventListener? = null
    var isRunning = false
    private var emitterFailedCount = 0
    private var globalEmiiterErrorCount:Int = 0

    //run in main thread
    fun addPointsToEmitterQueue(points: List<RabbitReportInfo>) {
        listModifierLock.lock()
        try {
            trackPointsList.addAll(points)
        } finally {
            listModifierLock.unlock()
        }
    }

    fun addPoint(pointInfo: RabbitReportInfo) {
        listModifierLock.lock()
        try {
            //避免堆积大量的点
            if (trackPointsList.size < EMITER_QUEUE_MAX_SIZE) {
                trackPointsList.add(pointInfo)
                RabbitLog.d(TAG_REPORT, "add point.. current point count : ${trackPointsList.size}")
            }
        } finally {
            listModifierLock.unlock()
        }
    }

    fun emitterPoints() {
        if (globalEmiiterErrorCount > 3){
            RabbitLog.d(TAG_REPORT, "发射器异常 ! ")
            return
        }

        if (emitterFailedCount > MAX_EMITTER_FAILED_COUNT) {
            RabbitLog.d(TAG_REPORT, "超出最大发送失败次数!")
            isRunning = false
            emitterFailedCount = 0
            globalEmiiterErrorCount++
            return
        }

        RabbitLog.d(TAG_REPORT, "EmitterTask Start")

        isRunning = true
        currentSleepCount = 0
        if (trackPointsList.isEmpty()) {
            eventListener?.pointQueueIsEmpty()
            RabbitLog.d(TAG_REPORT, "EmitterTask Stop")
            isRunning = false
        } else {
            circleEmitterPoint()
        }
    }

    private fun circleEmitterPoint() {
        val currentPointCount = trackPointsList.size
        if (currentPointCount > BATCH_EMITTER_NUMBER) {
            emitterTrackPoints()
        } else {
            currentSleepCount++
            if (currentSleepCount < MAX_SLEEP_COUNT) {
                RabbitLog.d(
                    TAG_REPORT,
                    "point count : ${trackPointsList.size}, current sleep : ${currentSleepCount}， continue sleeping"
                )
                Thread.sleep(CIRCLE_EMITTER_TIME)
                circleEmitterPoint()
            } else {
                if (trackPointsList.isNotEmpty()) {
                    emitterTrackPoints()
                } else {
                    isRunning = false
                    RabbitLog.d(TAG_REPORT, "EmitterTask Stop")
                }
            }
        }
    }

    // 运行在网络请求线程
    private fun emitterTrackPoints() {
        if (trackPointsList.isEmpty()) return

        RabbitLog.d(TAG_REPORT, "emitterTrackPoints  ${trackPointsList.size}")

        val lastPointIndex =
            if (trackPointsList.size <= BATCH_EMITTER_NUMBER) trackPointsList.size else BATCH_EMITTER_NUMBER

        //copy出来一份, 防止并发操作引起异常
        val copiedList = ArrayList<RabbitReportInfo>()
        listModifierLock.lock()
        try {
            trackPointsList.subList(0, lastPointIndex).forEach {
                copiedList.add(
                    RabbitReportInfo(
                        it.id,
                        it.infoStr,
                        it.time,
                        it.deviceInfoStr,
                        it.type,
                        it.useTime
                    )
                )
            }
        } finally {
            listModifierLock.unlock()
        }

        RabbitLog.d(TAG_REPORT, "emitterTrackPoints copiedList size :  ${copiedList.size}")

        trackRequest.postTrackRequest(
            copiedList,
            object : RabbitReportRequestManager.TrackRequestListener {

                //run in emitter thread
                override fun onRequestSucceed() {
                    var emitterWithError = false //发射逻辑出现错误
                    listModifierLock.lock()
                    try {
                        emitterFailedCount = 0
                        copiedList.forEach {
                            val removeStatus = trackPointsList.remove(it)
                            if (!removeStatus) { // 没有正确把点从队列中删除
                                RabbitLog.d(
                                    TAG_REPORT,
                                    "remove emitter point status false!!  stop emitter"
                                )
                                emitterWithError = true
                            }
                            eventListener?.successEmitterPoint(it)
                        }
                    } finally {
                        listModifierLock.unlock()
                    }

                    if (trackPointsList.isNotEmpty()) {
                        if (!emitterWithError) {
                            emitterTrackPoints()
                        } else {
                            RabbitLog.d(TAG_REPORT, "emitterWithError is true --> stop emitter!")
                        }
                    } else {
                        RabbitLog.d(TAG_REPORT, "emitter all count ! EmitterTask Stop")
                        isRunning = false
                        eventListener?.pointQueueIsEmpty()
                    }
                }

                override fun onRequestFailed() {
                    emitterFailedCount++
                    RabbitLog.d(
                        TAG_REPORT,
                        "emitter to server failed , emitterFailedCount : $emitterFailedCount"
                    )
                    emitterPoints()
                }
            })

    }

    interface EventListener {
        fun pointQueueIsEmpty()
        fun successEmitterPoint(pointInfo: RabbitReportInfo)
    }

}