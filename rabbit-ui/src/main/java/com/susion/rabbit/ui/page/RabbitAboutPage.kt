package com.susion.rabbit.ui.page

import android.content.Context
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R

/**
 * create by susionwang at 2020-01-10
 */
class RabbitAboutPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_about

    init {
        setTitle("About")
    }

}