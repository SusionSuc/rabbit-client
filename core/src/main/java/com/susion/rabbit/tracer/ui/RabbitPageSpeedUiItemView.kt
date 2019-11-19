package com.susion.rabbit.tracer.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.tracer.entities.RabbitBlockFrameInfo
import com.susion.rabbit.tracer.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.utils.*
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitPageSpeedUiItemView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitPageSpeedUiInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_ui_block_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(uiInfo: RabbitPageSpeedUiInfo, position: Int) {

        mRabbitUiBlockItemViewTvLine1.text = uiInfo.pageName
        mRabbitUiBlockItemViewTvLine2.text = "页面测试记录数 : ${uiInfo.speedInfoList.size}"

        throttleFirstClick(Consumer {
            Rabbit.uiManager.openPage(RabbitPageSpeedDetailPage::class.java, uiInfo)
        })
    }

}