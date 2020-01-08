package com.susion.rabbit.gradle.entities

/**
 * susionwang at 2019-11-28
 */
open class RabbitConfigExtension {
    var pageSpeedMonitorPkgs: List<String> = ArrayList<String>()

    var methodMonitorPkgs: List<String> = ArrayList<String>()

    var ioScanPkgs: List<String> = ArrayList<String>()

    var enable: Boolean = true
    var printLog: Boolean = true
}