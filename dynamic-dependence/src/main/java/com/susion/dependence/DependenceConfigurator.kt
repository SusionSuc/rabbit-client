package com.susion.dependence

import com.google.gson.Gson
import com.susion.dependence.entities.DependenceConfigInfo
import com.susion.dependence.entities.DependenceInfo
import org.gradle.api.Project

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class DependenceConfigurator {

    private lateinit var mRootProject: Project

    fun configProjectDependence(rootProject: Project, config: ConfigExtension?) {

        if (config == null) {
            Helper.printE("null config")
            return
        }

        mRootProject = rootProject

        if (config.config_file_path.isNullOrEmpty()) {
            Helper.printE("null or empty  file path")
            return
        }

        Helper.printE("dependence file path : ${config.config_file_path}")

        val configFile = rootProject.file(config.config_file_path)


        if (!configFile.exists()) {
            Helper.printE("config not exists!")
            return
        }

        val dependenceStr = configFile.readText(Charsets.UTF_8)

        Helper.printE("dependence config str: $dependenceStr")

        try {
            val configInfo = Gson().fromJson(dependenceStr, DependenceConfigInfo::class.java)
            configModules(configInfo)
        } catch (e: Exception) {
            Helper.printE("dependence config file parse error")
        }

    }

    private fun configModules(configInfo: DependenceConfigInfo) {
        mRootProject.allprojects { project ->
            configModuleOneByOne(mRootProject.project(":${project.name}"), configInfo)
        }
    }

    private fun configModuleOneByOne(moduleProject: Project?, configInfo: DependenceConfigInfo) {

        if (moduleProject == null) {
            Helper.printE("null moduleProject")
            return
        }

        Helper.printE("config project -> ${moduleProject.name}")

        val moduleDependence = configInfo.moduleList?.firstOrNull { it.name == moduleProject.name }

        if (moduleDependence == null) {
            Helper.printE("not find project ->  ${moduleProject.name}  dependence config")
            return
        }

        moduleDependence.dependenceList?.forEach {
            configDependenceForModule(moduleProject, it)
        }

    }

    private fun configDependenceForModule(project: Project, dependence: DependenceInfo) {
        when (dependence.method) {
            DependenceInfo.METHOD_IMPLEMENTATION -> {
                configImplement(project, dependence)
            }
            else -> {
                Helper.printE(" method not support : ${dependence.method} ")
            }
        }
    }

    private fun configImplement(project: Project, dependence: DependenceInfo) {
        when (dependence.type) {
            DependenceInfo.TYPE_PROJECT -> {
                project.dependencies.add(
                    DependenceInfo.METHOD_IMPLEMENTATION,
                    mRootProject.project(":${dependence.path}")
                )
                Helper.printD(" success config : method -> ${dependence.method}; type -> ${dependence.type} ; path -> ${dependence.path}")

            }
            else -> {
                Helper.printE(" type not support ${dependence.type} ")
            }
        }
    }

}