package com.susion.rabbit.ui.monitor.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.base.entities.RabbitAppStartSpeedTotalInfo
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.monitor.page.RabbitAppStartSpeedDetailPage
import com.susion.rabbit.ui.base.throttleFirstClick
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_app_speed_info.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitAppSpeedInfoView(context: Context) : LinearLayout(context),
    com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView<RabbitAppStartSpeedTotalInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_app_speed_info, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(uiInfo: RabbitAppStartSpeedTotalInfo, position: Int) {
        mRabbitAppSpeedTvAvgOnCreate.text = uiInfo.avgOnCreateTime
        mRabbitAppSpeedTvAvgFullTime.text = uiInfo.avgFullStartTime
        mRabbitAppSpeedTvLogNumber.text = uiInfo.count

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitAppStartSpeedDetailPage::class.java)
        })
    }

}