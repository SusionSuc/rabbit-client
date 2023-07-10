package com.susion.rabbit.base.config

import androidx.annotation.DrawableRes
import android.view.View

/**
 * susionwang at 2019-10-12
 */
class RabbitMainFeatureInfo(
    val name: String,
    val type: String,
    @DrawableRes val icon: Int,
    val pageClass: Class<out View>? = null,
    val action: () -> Unit = {}
) {
    companion object {
        const val TYPE_BUSINESS = "业务"
        const val TYPE_MONITOR = "监控"
        const val TYPE_OPTIMIZER = "优化"
        const val TYPE_CONFIG = "配置"
    }
}