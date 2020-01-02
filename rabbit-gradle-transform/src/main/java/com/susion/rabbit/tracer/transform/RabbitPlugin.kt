package com.susion.rabbit.tracer.transform

import com.android.build.gradle.AppExtension
import com.susion.rabbit.tracer.transform.core.rxentension.getAndroid
import com.susion.rabbit.tracer.transform.entities.RabbitConfigExtension
import com.susion.rabbit.tracer.transform.utils.RabbitTransformUtils
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
            GlobalConfig.pageSpeedMonitorPkgs = config.pageSpeedMonitorPkgs
            GlobalConfig.methodMonitorPkgs = config.methodMonitorPkgs
            RabbitTransformUtils.print("custom  config monitor pkg name prefix :${GlobalConfig.pageSpeedMonitorPkgs} ")
        }

        //register transform
        project.getAndroid<AppExtension>().apply {
            registerTransform(RabbitTransform())
        }

    }


}