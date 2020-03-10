package com.susion.rabbit.gradle.utils

import com.susion.rabbit.gradle.GlobalConfig

/**
 * susionwang at 2019-11-12
 */

object RabbitTransformUtils {


    fun print(msg: String) {
        if (!GlobalConfig.pluginConfig.printLog) return
        kotlin.io.print("🐰 -----> $msg \n")
    }

    fun print(tag: String, msg: String) {
        if (!GlobalConfig.pluginConfig.printLog) return
        kotlin.io.print("🐰 -----> TAG : $tag ; $msg \n")
    }


    fun classInPkgList(className: String, pkgList: List<String>): Boolean {

        if (pkgList.isEmpty()) return false

        pkgList.forEach {
            if (className.startsWith(it)) {
                return true
            }
        }
        return false
    }
}