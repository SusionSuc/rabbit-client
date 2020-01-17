package com.susion.rabbit.ui.global.entities

/**
 * susionwang at 2020-01-17
 */
class RabbitAppSmoothEvaluateInfo(
    var totalSmooth: Float = 0f,
    var fps: Float = 0f,
    var mem: Float = 0f,
    var applicationCreate: Float = 0f,
    var appColdStart: Float = 0f,
    var pageInflate: Float = 0f,
    var block: Float = 0f,
    var slowMethod: Float = 0f
){
    override fun toString(): String {
        return "RabbitAppSmoothEvaluateInfo(totalSmooth=$totalSmooth, fps=$fps, mem=$mem, applicationCreate=$applicationCreate, appColdStart=$appColdStart, pageInflate=$pageInflate, block=$block, slowMethod=$slowMethod)"
    }
}