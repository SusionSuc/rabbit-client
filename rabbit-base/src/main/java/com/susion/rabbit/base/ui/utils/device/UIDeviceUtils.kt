package com.susion.rabbit.base.ui.utils.device

import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * susionwang at 2019-12-30
 */
object UIDeviceUtils {
    /**
     * checking if is huawei rom
     */
    fun checkIsHuaweiRom(): Boolean {
        return Build.MANUFACTURER.contains("HUAWEI")
    }

    /**
     * checking if is miui rom
     */
    fun checkIsMiuiRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    /**
     * checking if is meizu rom
     */
    fun checkIsMeizuRom(): Boolean {
        val meizuFlymeOSFlag = getSystemProperty("ro.build.display.id")
        return if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            false
        } else meizuFlymeOSFlag!!.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")
    }

    /**
     * checking if is 360 rom
     */
    fun checkIs360Rom(): Boolean {
        return Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360")
    }

    /**
     * checking if is oppo rom
     */
    fun checkIsOppoRom(): Boolean {
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo")
    }

    /**
     * get version of emui
     */
    fun getEmuiVersion(): Double {
        try {
            val emuiVersion = getSystemProperty("ro.build.version.emui")
            val version = emuiVersion!!.substring(emuiVersion.indexOf("_") + 1)
            return java.lang.Double.parseDouble(version)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 4.0
    }

    /**
     * get xiaomi version of emui
     */
    fun getMiuiVersion(): Int {
        val version = getSystemProperty("ro.miui.ui.version.name")
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1))
            } catch (e: Exception) {

            }
        }
        return -1
    }


    /**
     * get system property for "ro.build.version.emui"
     */
    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {

                }
            }
        }
        return line
    }
}