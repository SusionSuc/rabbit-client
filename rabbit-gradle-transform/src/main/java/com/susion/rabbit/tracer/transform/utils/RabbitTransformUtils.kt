package com.susion.rabbit.tracer.transform.utils

/**
 * susionwang at 2019-11-12
 */

object RabbitTransformUtils {

    private var enable = true

    fun print(msg:String){
        if (!enable) return
        kotlin.io.print("🐰 -----> $msg \n")
    }

    fun print(tag:String, msg:String){
        if (!enable) return
        kotlin.io.print("🐰 -----> TAG : ${tag} ; $msg \n")
    }


    fun classInPkgList(className:String, pkgList:List<String>):Boolean{

        val configList = pkgList

        if (configList.isEmpty()) return true

        configList.forEach {
            if (className.startsWith(it)) {
                return true
            }
        }
        return false
    }
}