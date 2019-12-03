package com.susion.rabbit.performance.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.performance.entities.RabbitBlockFrameInfo
import com.susion.rabbit.utils.*
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitUiBlockItemView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitBlockFrameInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_ui_block_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    override fun bindData(blockInfo: RabbitBlockFrameInfo, position: Int) {
//        val ms = TimeUnit.MILLISECONDS.convert(blockInfo.tr , TimeUnit.NANOSECONDS)
        mRabbitUiBlockItemViewTvLine1.text = "time:${rabbitSimpleTimeFormat(blockInfo.time)}"
        mRabbitUiBlockItemViewTvLine2.text = "${blockInfo.blockIdentifier}"

        throttleFirstClick(Consumer {
            Rabbit.uiManager.openPage(RabbitUiBlockDetailPage::class.java, blockInfo)
        })

    }

}