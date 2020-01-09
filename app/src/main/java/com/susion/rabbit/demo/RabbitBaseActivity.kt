package com.susion.rabbit.demo

import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import com.susion.rabbit.R
import com.susion.rabbit.base.ui.view.RabbitActionBar

/**
 * susionwang at 2019-09-25
 * uniform material design ui
 */
open class RabbitBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(
            window, com.susion.rabbit.base.ui.getColor(this, R.color.rabbit_material_dark)
        )
        setStatusBarTextColor(window, true)
    }

    fun setBackListener(actionBar: RabbitActionBar) {
        actionBar.actionListener = object : RabbitActionBar.ActionListener {
            override fun onBackClick() {
                finish()
            }
        }
    }

    private fun setStatusBarColor(window: Window, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = color
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setStatusBarTextColor(window: Window, lightStatusBar: Boolean) {
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

    fun setActionBar(view: RabbitActionBar) {
        view.actionListener = object : RabbitActionBar.ActionListener {
            override fun onBackClick() {
                finish()
            }
        }
    }

}