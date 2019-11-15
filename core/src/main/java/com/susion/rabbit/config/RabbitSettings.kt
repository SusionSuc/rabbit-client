package com.susion.rabbit.config

import android.content.Context
import android.content.SharedPreferences

/**
 * susionwang at 2019-09-24
 */
internal object RabbitSettings {

    private val SP_NAME = "dev_tools"
    private val AUTO_OPEN_FPS_CHECK = "auto_open_fps_check"
    private val AUTO_OPEM_BLOACK_CHECK = "auto_open_block_check"
    private val AUTO_OPEN_ACTIVITY_SPEED_CHECK = "auto_open_ac_speed_check"

    fun setFPSCheckOpenFlag(context: Context, autoOpen: Boolean) {
        setBooleanValue(context, AUTO_OPEN_FPS_CHECK, autoOpen)
    }

    fun setBlockCheckOpenFlag(context: Context, autoOpen: Boolean){
        setBooleanValue(context, AUTO_OPEM_BLOACK_CHECK, autoOpen)
    }

    fun setActivitySpeedMonitorOpenFlag(context: Context, autoOpen: Boolean){
        setBooleanValue(context, AUTO_OPEN_ACTIVITY_SPEED_CHECK, autoOpen)
    }

    fun blockCheckAutoOpen(context: Context) = getSp(context).getBoolean(AUTO_OPEM_BLOACK_CHECK, false)

    fun fpsCheckAutoOpenFlag(context: Context) = getSp(context).getBoolean(AUTO_OPEN_FPS_CHECK, false)

    fun acSpeedMonitorOpenFlag(context: Context) =  getSp(context).getBoolean(AUTO_OPEN_ACTIVITY_SPEED_CHECK, false)

    private fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    private fun getSpEdit(context: Context): SharedPreferences.Editor {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
    }

    private fun setBooleanValue(context: Context, key:String, value:Boolean){
        getSpEdit(context).putBoolean(key,value).commit()
    }

}