package com.susion.rabbit.ui.global

import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.ui.global.entities.RabbitAppSmoothEvaluateInfo
import com.susion.rabbit.ui.global.entities.RabbitAppPerformanceOverviewInfo

/**
 * 计算应用当前流畅值, 满分为100分。
 *
 * 算法逻辑:
 *
 * 参与计算的几个指标所占的百分比:
 * 1. 平均帧率
 * 2. 平均内存值
 * 3. 应用Application创建时间
 * 4. 应用冷启动耗时 -> 如果统计不到直接满分
 * 5. 页面平均inflate时间
 * 6. 卡顿次数
 * 7. 慢函数数量
 * */
object RabbitAppSmoothEvaluator {

    const val FPS = 25f
    const val MEM = 10f
    const val APPLICATION_CREATE = 5f
    const val APP_COLD_START = 10f
    const val PAGE_INFLATE = 20f
    const val BLOCK = 10f
    const val SLOW_METHOD = 20f

    fun evaluateSmoothScore(monitorInfo: RabbitAppPerformanceOverviewInfo): RabbitAppSmoothEvaluateInfo {

        val evaluateInfo = RabbitAppSmoothEvaluateInfo()

        var totalScore = 0f

        //FPS
        var fpsScore = FPS
        val avgFps = monitorInfo.avgFps.toLong()
        val fpsRadio = when{
            avgFps > 50 -> 1f
            avgFps > 40 -> 0.7f
            avgFps > 30 -> 0.4f
            else -> 0f
        }
        fpsScore *= fpsRadio
        totalScore += fpsScore
        evaluateInfo.fps = fpsScore

        //内存占用
        val maxMemLimit = Runtime.getRuntime().maxMemory()
        val avgVmMem = monitorInfo.avgJVMMemory
        val memTakeUp = avgVmMem * 1f / maxMemLimit
        var memScore = MEM - (MEM * memTakeUp)
        if (memTakeUp < (avgVmMem * 2f / 5)){ // 小于最大内存的 2/5 直接满分
            memScore = MEM
        }
        totalScore += memScore
        evaluateInfo.mem = memScore

        // application创建时间
        val maxApplicationCreateTime = 1500
        var applicationCreateScore = APPLICATION_CREATE - (APPLICATION_CREATE * (monitorInfo.applicationCreateTime * 1f / maxApplicationCreateTime))
        if (monitorInfo.applicationCreateTime < 500){  // 小于500ms直接满分
            applicationCreateScore = APPLICATION_CREATE
        }
        totalScore += applicationCreateScore
        evaluateInfo.applicationCreate = applicationCreateScore

        // 冷启动时间
        val maxColdStartTime = 5000
        var coldStartScore = APP_COLD_START - (APP_COLD_START * (monitorInfo.appColdStartTime * 1f / maxColdStartTime))
        if (monitorInfo.appColdStartTime < 1500){  //小于2S直接满分
            coldStartScore = APP_COLD_START
        }
        totalScore += coldStartScore
        evaluateInfo.appColdStart = coldStartScore

        //页面inflate
        val maxInflateTime = 300
        var inflateScore = PAGE_INFLATE - (PAGE_INFLATE * (monitorInfo.pageAvgInflateTime * 1f / maxInflateTime))
        if (monitorInfo.pageAvgInflateTime < 100){
            inflateScore = PAGE_INFLATE
        }
        totalScore += inflateScore
        evaluateInfo.pageInflate = inflateScore

        //卡顿
        val maxBlockCount = 10
        val blockScore = BLOCK - (BLOCK * (monitorInfo.blockCount * 1f / maxBlockCount))
        totalScore += blockScore
        evaluateInfo.block = blockScore

        //慢函数
        val maxSlowMethod = 20
        val slowMethodScore = SLOW_METHOD - (SLOW_METHOD * (monitorInfo.slowMethodCount * 1f / maxSlowMethod))
        totalScore += slowMethodScore
        evaluateInfo.slowMethod = slowMethodScore

        evaluateInfo.totalSmooth = totalScore

        RabbitLog.d(TAG_MONITOR_UI, "evaluate info : $evaluateInfo")

        return evaluateInfo
    }

}