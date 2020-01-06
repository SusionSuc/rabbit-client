package com.susion.rabbit.gradle.core

import com.susion.rabbit.gradle.core.context.TransformContext

/**
 * susionwang at 2019-11-13
 */
interface TransformListener {

    fun onPreTransform(context: TransformContext){}

    fun onPostTransform(context: TransformContext){}

}
