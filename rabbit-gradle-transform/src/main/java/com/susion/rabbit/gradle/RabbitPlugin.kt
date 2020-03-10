package com.susion.rabbit.gradle

import com.android.build.gradle.AppExtension
import com.susion.rabbit.gradle.core.rxentension.getAndroid
import com.susion.rabbit.gradle.entities.RabbitConfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * susionwang at 2019-11-12
 *
 * rabbit plugin entry
 */
class RabbitPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        //load custom config
        val config = project.extensions.create("rabbitConfig", RabbitConfigExtension::class.java)

        project.afterEvaluate {
            //如果配置的检测包的范围，则自动打开对应开关
            config.enableBlockCodeCheck = config.customBlockCodeCheckList.isNotEmpty()
            config.enableMethodCostCheck = config.methodMonitorPkgs.isNotEmpty()

            GlobalConfig.pluginConfig = config
            GlobalConfig.ioMethodCall.clear()
        }

        //register transform
        project.getAndroid<AppExtension>().apply {
            registerTransform(RabbitFirstTransform())
            registerTransform(RabbitLastTransform())
        }
    }

}