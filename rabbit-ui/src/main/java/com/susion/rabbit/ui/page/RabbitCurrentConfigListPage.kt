package com.susion.rabbit.ui.page

import android.content.Context
import com.google.gson.Gson
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_current_config_list.view.*

/**
 * create by susionwang at 2020-01-10
 */
class RabbitCurrentConfigListPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_current_config_list

    init {
        if (RabbitUi.externalDataRequest != null) {
            mRabbitCurrentConfigPageJsonRv.bindJson(RabbitUi.externalDataRequest?.getGlobalConfigJsonStr())
        }
    }

}