package com.susion.dependence

import java.io.*

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */

internal object Helper {

    const val TAG_E = "E->  dynamic-dependence"
    const val TAG_D = "D->  dynamic-dependence"


    fun printE(str: String) {
        println("$TAG_E ----> $str")
    }


    fun printD(str: String){
        println("$TAG_D ----> $str")
    }
}