package com.susion.rabbit.base.ui.view

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import com.susion.rabbit.base.R
import com.susion.rabbit.base.ui.getColor

/**
 * susionwang at 2020-01-08
 */

class RabbitSimpleTipDialog(activity: Activity, val tipStr: String) :
    Dialog(activity, R.style.RabbitSimpleDialog) {

    private var mHeight: Int = 0
    private var sIsShown = false
    private val tipTv = TextView(activity).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        setTextColor(getColor(activity, R.color.rabbit_black))
        text = tipStr
        setLineSpacing(0f, 1.5f)
    }

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(tipTv)
    }

    override fun show() {
        if (sIsShown) {
            return
        }
        val window = window

        if (window != null) {
            window.setGravity(Gravity.CENTER)
            val lp = window.attributes
            lp.width = Resources.getSystem().displayMetrics.widthPixels
            if (mHeight > 0) {
                lp.height = mHeight
            }
            window.attributes = lp
        }
        super.show()
        sIsShown = true
    }


}