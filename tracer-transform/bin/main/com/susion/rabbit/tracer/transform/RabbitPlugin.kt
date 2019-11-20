package com.susion.rabbit.tracer.transform

import com.android.build.gradle.AppExtension
import com.susion.rabbit.tracer.transform.core.rxentension.getAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * susionwang at 2019-11-12
 *
 * rabbit plugin entry
 */
class RabbitPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.getAndroid<AppExtension>().apply {
            registerTransform(RabbitTransform())
        }
    }

}