package com.susion.rabbit.gradle

import com.susion.rabbit.gradle.entities.RabbitConfigExtension

/**
 * susionwang at 2019-11-28
 */
object GlobalConfig {

    var pluginConfig: RabbitConfigExtension = RabbitConfigExtension()

    var ioMethodCall = ArrayList<String>()
}