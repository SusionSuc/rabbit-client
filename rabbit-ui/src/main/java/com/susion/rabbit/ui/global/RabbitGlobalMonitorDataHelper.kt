package com.susion.rabbit.ui.global

import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.global.entities.RabbitPageGlobalMonitorInfo
import com.susion.rabbit.ui.global.entities.RabbitGlobalModePreInfo
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2020-01-16
 * 解析全局监控数据
 */
object RabbitGlobalMonitorDataHelper {

    fun getGlobalMonitorPreInfo(monitorInfo: RabbitGlobalMonitorInfo): RabbitGlobalModePreInfo {

        val preInfo = RabbitGlobalModePreInfo(globalMonitorInfo = monitorInfo)

        preInfo.isRunning = monitorInfo.isRunning

        preInfo.recordStartTime = rabbitTimeFormat(monitorInfo.time)

        val durationS =
            TimeUnit.SECONDS.convert(monitorInfo.endTime - monitorInfo.time, TimeUnit.MILLISECONDS)
        preInfo.duration = "$durationS s"

        preInfo.avgFps = getAvgFps(monitorInfo.fpsIds)

        preInfo.avgJVMMemory = getAvgTotalMemory(monitorInfo.memoryIds)

        if (idIsValid(monitorInfo.appStartId)) {
            val appStartInfo = RabbitDbStorageManager.getObjSync(
                RabbitAppStartSpeedInfo::class.java,
                monitorInfo.appStartId.toLong()
            )
            if (appStartInfo != null) {
                preInfo.applicationCreateTime =
                    appStartInfo.createEndTime - appStartInfo.createStartTime
                preInfo.appColdStartTime = appStartInfo.fullShowCostTime
            }
        }

        preInfo.blockCount = getCalculateCount(monitorInfo.blockIds)

        preInfo.pageAvgInflateTime = getAvgPageInflateTime(monitorInfo.pageSpeedIds)

        preInfo.totalPageNumber = getCalculateCount(monitorInfo.pageSpeedIds)

        preInfo.slowMethodCount = getCalculateCount(monitorInfo.slowMethodIds)

        preInfo.smoothEvaluateInfo = RabbitAppSmoothEvaluator.evaluateSmoothScore(preInfo)

        return preInfo
    }

    private fun getAvgFps(ids: String?): Int {
        if (ids == null) return 0
        return getIds(ids)
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitFPSInfo::class.java,
                    id.toLong()
                )
            }.map { it.avgFps }.average().toInt()
    }

    private fun getAvgTotalMemory(ids: String?): Long {
        if (ids == null) return 0L
        return getIds(ids)
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitMemoryInfo::class.java,
                    id.toLong()
                )
            }.map { it.totalSize - it.nativeSize }.average().toLong()
    }

    private fun getAvgPageInflateTime(ids: String?): Long {
        if (ids == null) return 0L
        return getIds(ids)
            .mapNotNull { id ->
                RabbitDbStorageManager.getObjSync(
                    RabbitPageSpeedInfo::class.java,
                    id.toLong()
                )
            }.map { it.pageInflateTime }.average().toLong()
    }

    private fun getCalculateCount(ids: String?): Int {
        if (ids == null) return 0
        return getIds(ids).size
    }

    private fun getIds(ids: String) = ids.split("&").filter { idIsValid(it) }

    private fun idIsValid(id: String?): Boolean {
        return id?.toLongOrNull() != null
    }

    fun getPageMonitorInfos(monitorInfo: RabbitGlobalMonitorInfo): List<RabbitPageGlobalMonitorInfo> {

        val pageInfoMap = HashMap<String, RabbitPageGlobalMonitorInfo>()

        //fps
        getIds(monitorInfo.fpsIds).mapNotNull { id ->
            RabbitDbStorageManager.getObjSync(
                RabbitFPSInfo::class.java,
                id.toLong()
            )
        }.forEach { fpsInfo ->
            val pageInfo = createInfoNotExist(pageInfoMap, fpsInfo.pageName)
            pageInfo.fpsCount++
            pageInfo.avgFps = getNewAvgValue(pageInfo.avgFps, fpsInfo.avgFps, pageInfo.fpsCount)
        }

        //mem
        getIds(monitorInfo.memoryIds).mapNotNull { id ->
            RabbitDbStorageManager.getObjSync(
                RabbitMemoryInfo::class.java,
                id.toLong()
            )
        }.forEach { memInfo ->
            val pageInfo = createInfoNotExist(pageInfoMap, memInfo.pageName)
            pageInfo.memCount++
            val memSize = memInfo.totalSize - memInfo.nativeSize
            pageInfo.avgMem = getNewAvgValue(pageInfo.avgMem, memSize, pageInfo.memCount)
        }

        return pageInfoMap.values.toList()
    }

    private fun getNewAvgValue(lastAvg: Int, newValue: Int, num: Int): Int {
        return if (num <= 1) {
            newValue
        } else {
            ((lastAvg * (num - 1)) + newValue) / num
        }
    }

    private fun createInfoNotExist(
        pageInfoMap: HashMap<String, RabbitPageGlobalMonitorInfo>,
        pageName: String
    ): RabbitPageGlobalMonitorInfo {
        if (pageInfoMap[pageName] == null) {
            pageInfoMap[pageName] = RabbitPageGlobalMonitorInfo()
        }
        return pageInfoMap[pageName]!!
    }
}