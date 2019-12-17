package com.susion.rabbit.report

import com.google.gson.Gson
import com.susion.rabbit.entities.*
import com.susion.rabbit.report.entities.RabbitSimpleBlockInfo

/**
 * susionwang at 2019-12-16
 * 对客户端采集的数据做一些转换，然后上报到服务端
 */
object RabbitReportTransformCenter {

    fun createReportInfo(info: Any, appUseTime: Long): RabbitReportInfo {
        val reportInfo =  RabbitReportInfo(
            getRealInfoStr(info),
            System.currentTimeMillis(),
            RabbitReport.getDeviceInfoStr(),
            getDataType(info),
            appUseTime
        )

        if (reportInfo.deviceInfoStr.isEmpty()){
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
            else -> "undefine"
        }
    }

    private fun getRealInfoStr(info: Any): String {
        val gson = Gson()
        var infoStr = ""

        when (info) {
            is RabbitPageSpeedInfo, is RabbitAppStartSpeedInfo, is RabbitFPSInfo -> {
                infoStr = gson.toJson(info)
            }

            is RabbitBlockFrameInfo -> {
                infoStr =
                    gson.toJson(
                        RabbitSimpleBlockInfo(
                            info.costTime,
                            info.blockPage,
                            info.time,
                            info.blockIdentifier
                        )
                    )
            }
        }
        return infoStr
    }

}