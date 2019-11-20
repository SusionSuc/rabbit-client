package com.susion.rabbit.tracer.ui

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.tracer.entities.RabbitAppStartSpeedTotalInfo
import com.susion.rabbit.utils.*
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_app_speed_info.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitAppSpeedInfoView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitAppStartSpeedTotalInfo> {

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
            Rabbit.uiManager.openPage(RabbitAppStartSpeedDetailPage::class.java)
        })
    }

}