package com.susion.rabbit.gradle.entities

/**
 * susionwang at 2019-11-28
 *
 * plugin dsl config
 */
open class RabbitConfigExtension {

    var pageSpeedMonitorPkgs: List<String> = ArrayList()  //测速扫描范围

    var methodMonitorPkgs: List<String> = ArrayList() // 函数耗时扫描范围

    var blockCodePkgs: List<String> = ArrayList()

    var enable: Boolean = true // 是否启动整个插件

    var printLog: Boolean = true

    var enableSpeedCheck: Boolean = true // 应用测速

    var enableBlockCodeCheck: Boolean = false  // 阻塞代码调用

    var enableMethodCostCheck: Boolean = false //方法耗时

    var customBlockCodeCheckList: Set<String> = HashSet() // 自定义阻塞代码检测列表

}