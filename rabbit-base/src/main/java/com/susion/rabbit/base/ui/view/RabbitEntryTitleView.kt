package com.susion.rabbit.base.ui.view

import android.content.Context
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.config.RabbitEntryTitleInfo
import com.susion.rabbit.base.ui.utils.RabbitUiUtils

/**
 * wangpengcheng.wpc create at 2023/7/7
 * */
class RabbitEntryTitleView : LinearLayout, AdapterItemView<RabbitEntryTitleInfo> {

    val tvTitle by lazy {
        TextView(context).apply {
            setTextSize(15f)
            val padding = RabbitUiUtils.dip2Px(context, 5f).toInt()
            setPadding(padding, padding, 0, padding)
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        addView(tvTitle)
    }

    override fun bindData(data: RabbitEntryTitleInfo, position: Int) {
        tvTitle.setText(data.title)
    }
}