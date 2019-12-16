package com.susion.rabbit.tracer.transform.utils

/**
 * susionwang at 2019-11-12
 */

object RabbitTransformPrinter {

    private var enable = true

    fun p(msg:String){
        if (!enable) return
        print("🐰 -----> $msg \n")
    }

    fun p(tag:String, msg:String){
        if (!enable) return
        print("🐰 -----> TAG : ${tag} ; $msg \n")
    }

}