package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.base.common.rabbitSimpleTimeFormat
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.ui.page.RabbitUiBlockDetailPage
import com.susion.rabbit.ui.utils.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitUiBlockItemView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<com.susion.rabbit.base.entities.RabbitBlockFrameInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_ui_block_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(blockInfo: com.susion.rabbit.base.entities.RabbitBlockFrameInfo, position: Int) {
//        val ms = TimeUnit.MILLISECONDS.convert(blockInfo.tr , TimeUnit.NANOSECONDS)
        mRabbitUiBlockItemViewTvLine1.text = "time:${rabbitSimpleTimeFormat(
            blockInfo.time
        )}"
        mRabbitUiBlockItemViewTvLine2.text = "${blockInfo.blockIdentifier}"

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitUiBlockDetailPage::class.java, blockInfo)
        })

    }

}