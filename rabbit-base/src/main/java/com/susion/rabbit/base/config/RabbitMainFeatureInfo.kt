package com.susion.rabbit.base.config

import androidx.annotation.DrawableRes
import android.view.View

/**
 * susionwang at 2019-10-12
 */
class RabbitMainFeatureInfo(
    val name: String,
    @DrawableRes val icon: Int,
    val pageClass: Class<out View>?,
    val action:()->Unit = {}
)