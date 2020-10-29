package com.susion.dependence.entities

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class DependenceInfo(
    val method: String,
    val type: String,
    val path: String
) {
    companion object {
        const val METHOD_IMPLEMENTATION = "implementation"

        const val TYPE_PROJECT = "project"

    }
}