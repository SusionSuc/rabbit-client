package com.susion.rabbit.base.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import android.view.View
import android.view.Window
import java.util.*

/**
 * susionwang at 2019-09-23
 */
object RabbitUiUtils {

    private val sMetrics = Resources.getSystem().displayMetrics

    fun getScreenHeight(): Int {
        return sMetrics?.heightPixels ?: 0
    }

    fun getScreenWidth(): Int {
        return sMetrics?.widthPixels ?: 0
    }

    /**
     * get the color by id
     */
    fun getColor(context: Context?, resId: Int): Int {
        return if (null == context || context.resources == null || resId <= -1) {
            Color.TRANSPARENT
        } else ContextCompat.getColor(context, resId)
    }

    /*
    * 一种比较通用的 Status Bar Color , for Android 6.0 +
    * 魅族，小米 有特有的API。这里没做适配
    * */
    fun setGeneralStatusBarColor(window: Window) {
        setStatusBarColor(window, Color.WHITE)
        setStatusBarTextColor(window, true)
    }

    private fun setStatusBarColor(window: Window, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = color
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }


    fun setStatusBarTextColor(window: Window, lightStatusBar: Boolean) {
        // 设置状态栏字体颜色 白色与深色
        val decor = window.decorView
        var ui = decor.systemUiVisibility
        ui = ui or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ui = if (lightStatusBar) {
                ui or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                ui and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
        decor.systemUiVisibility = ui
    }

    fun copyStrToClipBoard(context: Context, str: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(null, str)
        clipboard.primaryClip = clipData
    }

    fun formatFileSize(size: Long): String {
        if (size <= 0) return ""
        val formater = Formatter()
        return when {
            size < 1024 -> size.toString() + "B"
            size < 1024 * 1024 -> {
                formater.format("%.2f KB", size / 1024f).toString()
            }
            size < 1024 * 1024 * 1024 -> {
                formater.format("%.2f MB", size / 1024f / 1024f).toString()
            }
            else -> {
                formater.format("%.2f GB", size / 1024f / 1024f / 1024f).toString()
            }
        }
    }

    fun dropPackageName(str: String): String {
        val strSlice = str.split(".")
        if (strSlice.size < 3) return str
        return "${strSlice[strSlice.size - 2]}.${strSlice[strSlice.size - 1]}"
    }

    fun formatTimeDuration(durations: Long): String {
        if (durations < 60){
            return "$durations 秒"
        }

        val min = durations / 60
        val sec = durations % 60

        return "${min}分${sec}秒"
    }

}