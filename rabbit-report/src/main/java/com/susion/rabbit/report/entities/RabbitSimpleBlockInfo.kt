package com.susion.rabbit.report.entities

/**
 * susionwang at 2019-12-16
 */
data class RabbitSimpleBlockInfo(
    val blockTime: Long,
    val pageName: String,
    val time: Long,
    val identifier: String
)