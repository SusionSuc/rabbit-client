package com.susion.rabbit.config

import android.content.Context
import android.content.SharedPreferences

/**
 * susionwang at 2019-09-24
 */
internal object RabbitSettings {

    private val SP_NAME = "dev_tools"
    private val AUTO_OPEN_FPS_CHECK = "auto_open_fps_check"

    fun setFPSCheckOpenFlag(context: Context, autoOpen: Boolean) {
        getSp(context).edit().putBoolean(AUTO_OPEN_FPS_CHECK, autoOpen).commit()
    }

    fun fpsAutoOpenFlag(context: Context) = getSp(context).getBoolean(AUTO_OPEN_FPS_CHECK, false)

    private fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private fun getSpEdit(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
    }

}