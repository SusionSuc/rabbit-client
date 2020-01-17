package com.susion.rabbit.ui.page

import android.content.Context
import com.google.gson.Gson
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.RabbitUi
import kotlinx.android.synthetic.main.rabbit_page_current_config_list.view.*
import com.susion.rabbit.ui.monitor.R

/**
 * create by susionwang at 2020-01-10
 */
class RabbitCurrentConfigPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_current_config_list

    init {
        setTitle("当前配置详情")

        if (RabbitUi.eventListener != null) {
            val currentConfig = RabbitUi.eventListener?.getGlobalConfig()
            mRabbitCurrentConfigPageJsonRv.bindJson(Gson().toJson(currentConfig))
        }
    }

}