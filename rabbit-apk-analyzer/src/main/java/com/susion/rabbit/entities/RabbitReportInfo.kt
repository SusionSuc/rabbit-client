package com.susion.rabbit.entities

/**
 * susionwang at 2020-01-13
 */
class RabbitReportInfo(
    val device_info_str: String = "",
    val info_str: String = "",
    val time: Long = System.currentTimeMillis(),
    val type: String = "apk-analyzer",
    val use_time: Long = 0
)