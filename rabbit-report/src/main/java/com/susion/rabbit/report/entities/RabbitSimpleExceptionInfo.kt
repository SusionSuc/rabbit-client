package com.susion.rabbit.report.entities

/**
 * susionwang at 2019-12-19
 */
data class RabbitSimpleExceptionInfo(
    val time: Long,
    val exceptionName: String,
    val identifier: String,
    val thread: String,
    val pageName: String
)