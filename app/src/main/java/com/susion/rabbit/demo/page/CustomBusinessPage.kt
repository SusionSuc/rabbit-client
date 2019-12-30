package com.susion.rabbit.demo.page

import android.content.Context
import android.widget.TextView
import com.susion.rabbit.demo.R
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.getDrawable

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
        })

        setTitle("自定义业务面板")
    }

}