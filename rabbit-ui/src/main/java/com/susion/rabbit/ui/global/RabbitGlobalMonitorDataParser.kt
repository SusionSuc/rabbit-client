package com.susion.rabbit.ui.global

import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.global.entities.RabbitGlobalModePreInfo
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2020-01-16
 * 解析全局监控数据
 */
object RabbitGlobalMonitorDataParser {

    fun getGlobalMonitorPreInfo(monitorInfo: RabbitGlobalMonitorInfo): RabbitGlobalModePreInfo {

        val preInfo = RabbitGlobalModePreInfo()

        preInfo.isRunning = monitorInfo.isRunning

        preInfo.recordStartTime = rabbitTimeFormat(monitorInfo.time)

        val durationS = TimeUnit.SECONDS.convert(monitorInfo.endTime - monitorInfo.time, TimeUnit.MILLISECONDS)
        preInfo.duration = "$durationS s"

        preInfo.avgFps = getAvgFps(monitorInfo.fpsIds)

        preInfo.avgJVMMemory = getAvgTotalMemory(monitorInfo.memoryIds)

        if (idIsValid(monitorInfo.appStartId)) {
            val appStartInfo = RabbitDbStorageManager.getObjSync(
                RabbitAppStartSpeedInfo::class.java,
                monitorInfo.appStartId.toLong()
            )
            if (appStartInfo != null) {
                preInfo.applicationCreateTime = appStartInfo.createEndTime - appStartInfo.createStartTime
                preInfo.appColdStartTime = appStartInfo.fullShowCostTime
            }
        }

        preInfo.blockCount = getCalculateCount(monitorInfo.blockIds)

        preInfo.pageAvgInflateTime = getAvgPageInflateTime(monitorInfo.pageSpeedIds)

        preInfo.totalPageNumbe =getCalculateCount(monitorInfo.pageSpeedIds)

        preInfo.slowMethodCount = getCalculateCount(monitorInfo.slowMethodIds)

        preInfo.smoothEvaluateInfo = RabbitAppSmoothEvaluator.evaluateSmoothScore(preInfo)

        return preInfo
    }

    private fun getAvgFps(ids: String?): Int {
        if (ids == null) return 0
        return ids.split("&")
            .filter { idIsValid(it) }
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitFPSInfo::class.java,
                    id.toLong()
                )
            }.map { it.avgFps }.average().toInt()
    }

    private fun getAvgTotalMemory(ids: String?): Long {
        if (ids == null) return 0L
        return ids.split("&")
            .filter { idIsValid(it) }
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitMemoryInfo::class.java,
                    id.toLong()
                )
            }.map { it.totalSize }.average().toLong()
    }

    private fun getAvgPageInflateTime(ids: String?): Long {
        if (ids == null) return 0L
        return ids.split("&")
            .filter { idIsValid(it) }
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitPageSpeedInfo::class.java,
                    id.toLong()
                )
            }.map { it.pageInflateTime }.average().toLong()
    }

    private fun getCalculateCount(ids: String?): Int {
        if (ids == null) return 0
        return ids.split("&").filter { idIsValid(it) }.size
    }

    private fun idIsValid(id: String?): Boolean {
        return id?.toLongOrNull() != null
    }


}