package com.susion.rabbit.ui.page

import android.content.Context
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_anr_detail.view.*

/**
 * susionwang at 2020-03-19
 */
class RabbitANRDetailPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_anr_detail

    init {
        setTitle("ANR详情")
    }

    override fun setEntryParams(info: Any) {

        if (info !is RabbitAnrInfo) return

        mAnrDetailTvTitle.text = "${info.pageName}  ${rabbitTimeFormat(info.time)}"

        mAnrDetailTvStackStr.text = info.stackStr

    }


}