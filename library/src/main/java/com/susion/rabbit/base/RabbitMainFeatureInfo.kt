package com.susion.rabbit.base

import android.app.Activity
import android.support.annotation.DrawableRes

/**
 * susionwang at 2019-10-12
 */
class RabbitMainFeatureInfo(
    val name: String,
    @DrawableRes val icon: Int,
    val activityClass: Class<out Activity>?
)