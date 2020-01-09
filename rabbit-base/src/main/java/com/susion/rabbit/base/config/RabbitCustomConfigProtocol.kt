package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-07
 */
class RabbitCustomConfigProtocol(
    val configName: String,
    var isEnable: Boolean,
    var statusChangeCallBack: ConfigChangeListener? = null
) {
    interface ConfigChangeListener {
        fun onChange(newStatus: Boolean)
    }
}