package com.susion.rabbit.base.config

/**
 * susionwang at 2020-01-07
 */
class RabbitCustomConfigProtocol(
    val configName: String,
    val initStatus: Boolean,
    val statusChangeCallBack: ConfigChangeListener
) {
    interface ConfigChangeListener {
        fun onChange(newStatus: Boolean)
    }
}