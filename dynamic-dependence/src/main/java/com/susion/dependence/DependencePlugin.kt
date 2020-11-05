package com.susion.dependence

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class DependencePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        Helper.printE("dependence plugin apply at module : ${project.name}")

        val config = project.extensions.create("dynamicDependence", ConfigExtension::class.java)

        project.afterEvaluate {

            val rootProject = project.rootProject

            Helper.printE("rootProject Evaluate finish")

            val configurator = DependenceConfigurator()
            val configInfo =
                configurator.parseConfigInfo(rootProject, config) ?: return@afterEvaluate

            if (config.globalEnable) {
                configurator.configModules(rootProject, configInfo)
            } else {
                configurator.configModule(
                    configurator.getModule(rootProject, project.name),
                    configurator.findModuleDependenceFromConfig(project.name, configInfo)
                )
            }
        }
    }

}