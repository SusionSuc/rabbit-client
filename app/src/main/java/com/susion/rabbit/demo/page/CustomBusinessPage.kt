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
        setTitle("自定义业务面板")
        showEmptyView("快来加入你的自定义功能吧")
    }

}