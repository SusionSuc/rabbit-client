package com.susion.rabbit.ui.global

import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.global.entities.RabbitAppPerformancePitInfo
import com.susion.rabbit.ui.global.entities.RabbitPagePerformanceInfo
import com.susion.rabbit.ui.global.entities.RabbitAppPerformanceOverviewInfo
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2020-01-16
 * 解析全局监控数据
 */
object RabbitPerformanceTestDataAnalyzer {

    private val TAG = javaClass.simpleName

    fun getGlobalMonitorSimpleInfo(monitorInfo: RabbitAppPerformanceInfo): RabbitAppPerformancePitInfo {
        val simpleInfo = RabbitAppPerformancePitInfo(globalMonitorInfo = monitorInfo)

        simpleInfo.isRunning = monitorInfo.isRunning

        simpleInfo.recordStartTime = rabbitTimeFormat(monitorInfo.time)

        simpleInfo.duration =
            TimeUnit.SECONDS.convert(monitorInfo.endTime - monitorInfo.time, TimeUnit.MILLISECONDS)

        return simpleInfo
    }

    fun getGlobalMonitorPreInfo(monitorInfo: RabbitAppPerformanceInfo): RabbitAppPerformanceOverviewInfo {

        val preInfo = RabbitAppPerformanceOverviewInfo()

        preInfo.isRunning = monitorInfo.isRunning

        preInfo.recordStartTime = rabbitTimeFormat(monitorInfo.time)

        val durationS =
            TimeUnit.SECONDS.convert(monitorInfo.endTime - monitorInfo.time, TimeUnit.MILLISECONDS)
        preInfo.duration = "$durationS s"

        preInfo.avgFps = getAvgFps(monitorInfo.fpsIds)

        preInfo.avgJVMMemory = getAvgTotalMemory(monitorInfo.memoryIds)

        if (idIsValid(monitorInfo.appStartId)) {
            val appStartInfo = RabbitStorage.querySync(
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
                RabbitStorage.querySync(
                    RabbitFPSInfo::class.java,
                    id.toLong()
                )
            }.map { it.avgFps }.average().toInt()
    }

    private fun getAvgTotalMemory(ids: String?): Long {
        if (ids == null) return 0L
        return getIds(ids)
            .mapNotNull { id ->
                RabbitStorage.querySync(
                    RabbitMemoryInfo::class.java,
                    id.toLong()
                )
            }.map { it.totalSize - it.nativeSize }.average().toLong()
    }

    private fun getAvgPageInflateTime(ids: String?): Long {
        if (ids == null) return 0L
        return getIds(ids)
            .mapNotNull { id ->
                RabbitStorage.querySync(
                    RabbitPageSpeedInfo::class.java,
                    id.toLong()
                )
            }.map { it.pageInflateTime }.average().toLong()
    }

    private fun getCalculateCount(ids: String?): Int {
        if (ids == null) return 0
        return getIds(ids).size
    }

    private fun getIds(ids: String?) = ids?.split("&")?.filter { idIsValid(it) } ?: emptyList()

    private fun idIsValid(id: String?): Boolean {
        return id?.toLongOrNull() != null
    }

    fun getPageMonitorInfos(monitorInfo: RabbitAppPerformanceInfo): List<RabbitPagePerformanceInfo> {

        val pageInfoMap = HashMap<String, RabbitPagePerformanceInfo>()

        //fps
        getIds(monitorInfo.fpsIds).mapNotNull { id ->
            RabbitStorage.querySync(
                RabbitFPSInfo::class.java,
                id.toLong()
            )
        }.filter { it.pageName.isNotEmpty() }.forEach { fpsInfo ->
            val pageInfo = createInfoNotExist(pageInfoMap, fpsInfo.pageName)
            pageInfo.fpsCount++
            pageInfo.avgFps = getNewAvgValue(
                pageInfo.avgFps.toLong(),
                fpsInfo.avgFps.toLong(),
                pageInfo.fpsCount.toLong()
            ).toInt()
        }

        //mem
        getIds(monitorInfo.memoryIds).mapNotNull { id ->
            RabbitStorage.querySync(
                RabbitMemoryInfo::class.java,
                id.toLong()
            )
        }.filter { it.pageName.isNotEmpty() }.forEach { memInfo ->
            val pageInfo = createInfoNotExist(pageInfoMap, memInfo.pageName)
            pageInfo.memCount++
            val memSize = memInfo.totalSize
            pageInfo.avgMem =
                getNewAvgValue(pageInfo.avgMem, memSize.toLong(), pageInfo.memCount.toLong())
        }

        //block
        getIds(monitorInfo.blockIds).mapNotNull { id ->
            RabbitStorage.querySync(
                RabbitBlockFrameInfo::class.java,
                id.toLong()
            )
        }.filter { it.pageName.isNotEmpty() }.forEach { memInfo ->
            createInfoNotExist(pageInfoMap, memInfo.pageName).blockCount++
        }

        //slow method
        getIds(monitorInfo.slowMethodIds).mapNotNull { id ->
            RabbitStorage.querySync(
                RabbitSlowMethodInfo::class.java,
                id.toLong()
            )
        }.filter { it.pageName.isNotEmpty() }.forEach { info ->
            createInfoNotExist(pageInfoMap, info.pageName).slowMethodCount++
        }

        //inflate & render
        getIds(monitorInfo.pageSpeedIds).mapNotNull { id ->
            RabbitStorage.querySync(
                RabbitPageSpeedInfo::class.java,
                id.toLong()
            )
        }.filter { it.pageName.isNotEmpty() }.forEach { pageInfo ->
            val info = createInfoNotExist(pageInfoMap, pageInfo.pageName)
            info.pageCount++

            val inflateDus = pageInfo.inflateFinishTime - pageInfo.createStartTime
            val renderDus = pageInfo.fullDrawFinishTime - pageInfo.createStartTime
            info.avgInlfateTime = getNewAvgValue(
                info.avgInlfateTime.toLong(),
                inflateDus,
                info.pageCount.toLong()
            ).toInt()
            info.avgFullRenderTime = getNewAvgValue(
                info.avgFullRenderTime.toLong(),
                renderDus,
                info.pageCount.toLong()
            ).toInt()
        }

        return pageInfoMap.values.toList()

    }

    private fun getNewAvgValue(lastAvg: Long, newValue: Long, num: Long): Long {
        return if (num <= 1) {
            newValue
        } else {
            (((lastAvg * (num - 1)) + newValue) * 1.0f / num).toLong()
        }
    }

    private fun createInfoNotExist(
        pageInfoMap: HashMap<String, RabbitPagePerformanceInfo>,
        pageName_: String
    ): RabbitPagePerformanceInfo {
        if (pageInfoMap[pageName_] == null) {
            RabbitLog.d(TAG, "create page performance info $pageName_")
            pageInfoMap[pageName_] = RabbitPagePerformanceInfo(pageName = pageName_)
        }
        return pageInfoMap[pageName_]!!
    }

}