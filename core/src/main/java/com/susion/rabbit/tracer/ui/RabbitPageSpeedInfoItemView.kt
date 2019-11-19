package com.susion.rabbit.tracer.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.view.RabbitSimpleKvInfo
import com.susion.rabbit.tracer.entities.RabbitBlockFrameInfo
import com.susion.rabbit.tracer.entities.RabbitPageSpeedInfo
import com.susion.rabbit.tracer.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.utils.*
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_speed_info_item.view.*
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitPageSpeedInfoItemView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitPageSpeedInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_speed_info_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp2px(15f)
        }
        orientation = VERTICAL
        background = getDrawable(context, R.color.rabbit_white)
        setPadding(dp2px(15f), dp2px(5f), dp2px(15f), dp2px(5f))
    }

    override fun bindData(uiInfo: RabbitPageSpeedInfo, position: Int) {
        val createCost = "${uiInfo.createEndTime - uiInfo.createStartTime}ms"
        val renderCost = "${uiInfo.drawFinishTime - uiInfo.createStartTime}ms"
        mPageSpeedItemSpeedTv.text = "create cost:$createCost 🐰 render cost:$renderCost"
        mPageSpeedItemCreateTimeTv.text = rabbitSimpleTimeFormat(uiInfo.time)
    }

}