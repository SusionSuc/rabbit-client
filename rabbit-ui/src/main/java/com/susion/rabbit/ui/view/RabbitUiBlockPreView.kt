package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.common.rabbitSimpleTimeFormat
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitBlockDetailPage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitUiBlockPreView(context: Context) : RelativeLayout(context),
    AdapterItemView<RabbitBlockFrameInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_ui_block_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(blockInfo: RabbitBlockFrameInfo, position: Int) {

        mRabbitUiBlockItemViewTvLine1.text = "time:${rabbitSimpleTimeFormat(
            blockInfo.time
        )}"

        mRabbitUiBlockItemViewTvLine2.text = "${blockInfo.blockIdentifier}"

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitBlockDetailPage::class.java, blockInfo)
        })

    }
}