package com.susion.devtools.base

import android.app.Activity
import android.support.annotation.DrawableRes

/**
 * susionwang at 2019-10-12
 */
class DevToolsMainFeatureInfo(
    val name: String,
    @DrawableRes val icon: Int,
    val activityClass: Class<out Activity>?
)