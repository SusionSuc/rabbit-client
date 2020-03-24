package com.susion.rabbit.report

import com.google.gson.Gson
import com.susion.rabbit.base.entities.*
import com.susion.rabbit.report.entities.RabbitSimpleBlockInfo
import com.susion.rabbit.report.entities.RabbitSimpleExceptionInfo

/**
 * susionwang at 2019-12-16
 * 对客户端采集的数据做一些转换，然后上报到服务端
 */
internal object RabbitReportTransformCenter {

    fun createReportInfo(info: Any, appUseTime: Long): RabbitReportInfo {
        val reportInfo = RabbitReportInfo(
            getRealInfoStr(info),
            System.currentTimeMillis(),
            RabbitReport.getDeviceInfoStr(),
            getDataType(info),
            appUseTime
        )

        if (reportInfo.deviceInfoStr.isEmpty()) {
            reportInfo.deviceInfoStr = "{}"
        }

        return reportInfo
    }

    private fun getDataType(info: Any): String {
        return when (info) {
            is RabbitPageSpeedInfo -> "page_speed"
            is RabbitAppStartSpeedInfo -> "app_start"
            is RabbitBlockFrameInfo -> "block_info"
            is RabbitFPSInfo -> "fps_info"
            is RabbitExceptionInfo -> "exception_info"
            is RabbitSlowMethodInfo -> "slow_method"
            is RabbitAnrInfo -> "anr"
            else -> "undefine"
        }
    }

    private fun getRealInfoStr(info: Any): String {
        val gson = Gson()
        var infoStr = ""

        when (info) {
            is RabbitPageSpeedInfo, is RabbitAppStartSpeedInfo, is RabbitFPSInfo, is RabbitSlowMethodInfo -> {
                infoStr = gson.toJson(info)
            }

            is RabbitBlockFrameInfo -> {
                infoStr =
                    gson.toJson(
                        RabbitSimpleBlockInfo(
                            info.costTime,
                            info.pageName,
                            info.time,
                            info.blockIdentifier
                        )
                    )
            }

            is RabbitExceptionInfo -> {
                infoStr = gson.toJson(
                    RabbitSimpleExceptionInfo(
                        info.time,
                        info.exceptionName,
                        info.simpleMessage,
                        info.threadName,
                        info.pageName
                    )
                )
            }
        }
        return infoStr
    }

}