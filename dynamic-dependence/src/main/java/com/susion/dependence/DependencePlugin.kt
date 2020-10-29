package com.susion.dependence

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class DependencePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val config = project.extensions.create("dynamicDependence", ConfigExtension::class.java)
        project.afterEvaluate {
            DependenceConfigurator().configProjectDependence(project.rootProject, config)
        }
    }

}