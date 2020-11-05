package com.susion.dependence

import com.google.gson.Gson
import com.susion.dependence.entities.DependenceConfigInfo
import com.susion.dependence.entities.DependenceInfo
import com.susion.dependence.entities.ModuleDependence
import org.gradle.api.Project

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class DependenceConfigurator {

    fun parseConfigInfo(rootProject: Project, config: ConfigExtension?): DependenceConfigInfo? {

        if (config == null) {
            Helper.printE("null config")
            return null
        }

        if (config.configFilePath.isNullOrEmpty()) {
            Helper.printE("null or empty  file path")
            return null
        }

        Helper.printE("dependence file path : ${config.configFilePath}")

        val configFile = rootProject.file(config.configFilePath)

        if (!configFile.exists()) {
            Helper.printE("config not exists!")
            return null
        }

        val dependenceStr = configFile.readText(Charsets.UTF_8)

        Helper.printE("dependence config str: $dependenceStr")

        try {
            return Gson().fromJson(dependenceStr, DependenceConfigInfo::class.java)
        } catch (e: Exception) {
            Helper.printE("dependence config file parse error")
        }

        return null
    }

    /**
     * 配置所有的module
     * */
    fun configModules(rootProject: Project, configInfo: DependenceConfigInfo) {
        rootProject.allprojects { project ->
            configModule(
                getModule(rootProject, project.name),
                findModuleDependenceFromConfig(project.name, configInfo)
            )
        }
    }

    /**
     * 获取一个module
     * */
    fun getModule(rootProject: Project, moduleName: String): Project? {
        return rootProject.project(":$moduleName")
    }

    /**
     * 配置一个module
     * */
    fun configModule(module: Project?, moduleDependence: ModuleDependence?) {

        if (module == null) {
            Helper.printE("null moduleProject")
            return
        }

        Helper.printE("config project -> ${module.name}")

        moduleDependence?.dependenceList?.forEach {
            configDependence(module, it)
        }

    }

    fun findModuleDependenceFromConfig(
        moduleName: String,
        configInfo: DependenceConfigInfo
    ): ModuleDependence? {

        val moduleDependence = configInfo.moduleList?.firstOrNull { it.name == moduleName }

        if (moduleDependence == null) {
            Helper.printE("not find project ->  $moduleName dependence config")
            return null
        }

        return moduleDependence
    }


    private fun configDependence(module: Project, dependence: DependenceInfo) {
        when (dependence.method) {
            DependenceInfo.METHOD_IMPLEMENTATION -> {
                configImplement(module, dependence)
            }
            else -> {
                Helper.printE(" method not support : ${dependence.method} ")
            }
        }
    }

    private fun configImplement(module: Project, dependence: DependenceInfo) {
        Helper.printD(" success config : method -> ${dependence.method}; type -> ${dependence.type} ; path -> ${dependence.path}")
        val dependenceHandler = module.dependencies
        when (dependence.type) {
            DependenceInfo.TYPE_PROJECT -> {
                dependenceHandler.add(
                    DependenceInfo.METHOD_IMPLEMENTATION,
                    module.rootProject.project(":${dependence.path}")
                )
            }
            DependenceInfo.TYPE_MAVEN -> {
                dependenceHandler.add(
                    DependenceInfo.METHOD_IMPLEMENTATION,
                    dependence.path
                )
            }
            else -> {
                Helper.printE(" type not support ${dependence.type} ")
            }
        }
    }

}