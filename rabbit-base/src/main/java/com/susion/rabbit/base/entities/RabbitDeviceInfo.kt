package com.susion.rabbit.base.entities

/**
 * susionwang at 2019-12-05
 * 上报的设备信息
 */
data class RabbitDeviceInfo(
    var deviceName: String = "",
    var deviceId: String = "",
    var systemVersion: String = "",
    var appVersionCode: String = "",
    var memorySize: String = "",
    var rom: String = "",
    var supportAbi: String = "",   //cpu架构
    var isRoot:Boolean = false,
    var manufacturer:String = ""
)