package com.susion.rabbit.demo.page

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.susion.rabbit.demo.R
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.getDrawable

/**
 * susionwang at 2019-12-30
 */
class CustomBusinessPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = INVALID_RES_ID

    init {
        background = getDrawable(context, R.color.rabbit_white)
        addView(TextView(context).apply {
            text = "业务自定义页面"
            textSize = 20f
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
        })

        RabbitUiKernal.appCurrentActivity
        setTitle("自定义业务面板")
    }

}