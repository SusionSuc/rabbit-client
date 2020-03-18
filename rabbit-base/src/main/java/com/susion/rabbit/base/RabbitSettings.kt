package com.susion.rabbit.base

import android.content.Context
import android.content.SharedPreferences

/**
 * susionwang at 2019-09-24
 */
object RabbitSettings {

    private val SP_NAME = "rabbit_sp"
    private val AUTO_OPEN_RABBIT = "auto_open"

    fun autoOpenRabbit(context: Context, autoOpen: Boolean) {
        setBooleanValue(
            context,
            AUTO_OPEN_RABBIT,
            autoOpen
        )
    }

    fun autoOpenRabbit(context: Context) = getSp(context).getBoolean(
        AUTO_OPEN_RABBIT, false
    )

    fun autoOpen(context: Context, monitorName: String) =
        getSp(context).getBoolean(monitorName, false)

    fun setAutoOpenFlag(context: Context, monitorName: String, autoOpen: Boolean) {
        setBooleanValue(context, monitorName, autoOpen)
    }

    private fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private fun getSpEdit(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
    }

    private fun setBooleanValue(context: Context, key: String, value: Boolean) {
        getSpEdit(context).putBoolean(key, value).commit()
    }

}