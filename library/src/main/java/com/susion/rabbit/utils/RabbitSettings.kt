package com.susion.rabbit.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * susionwang at 2019-09-24
 */
internal object RabbitSettings {

    private val AUTO_SHOW = "dev_tools_auto_show"
    private val SP_NAME = "dev_tools"

    fun autoOpenDevTools(context: Context, auto: Boolean) {
        getSpEdit(context).putBoolean(AUTO_SHOW, auto).commit()
    }

    fun autoOpenDevTools(context: Context): Boolean {
        return getSp(context).getBoolean(AUTO_SHOW, false)
    }

    private fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private fun getSpEdit(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
    }

}