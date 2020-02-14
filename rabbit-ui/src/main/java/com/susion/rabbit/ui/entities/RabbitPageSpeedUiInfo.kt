package com.susion.rabbit.base.entities

/**
 * susionwang at 2019-11-19
 *
 * 一个页面测试信息,包含多条测试记录， 用于在UI中显示
 */
class RabbitPageSpeedUiInfo(
    var pageName: String = "",
    val speedInfoList :ArrayList<com.susion.rabbit.base.entities.RabbitPageSpeedInfo> = ArrayList()
)