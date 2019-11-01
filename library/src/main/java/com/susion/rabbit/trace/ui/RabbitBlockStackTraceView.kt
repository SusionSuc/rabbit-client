package com.susion.rabbit.trace.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.trace.frame.RabbitBlockStackTraceInfo
import com.susion.rabbit.utils.dp2px
import com.susion.rabbit.utils.getDrawable
import kotlinx.android.synthetic.main.rabbit_view_block_frame_item.view.*

/**
 * susionwang at 2019-09-25
 */

class RabbitBlockStackTraceView(context: Context) : LinearLayout(context),
    RabbitAdapterItemView<RabbitBlockStackTraceInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_block_frame_item, this)
        layoutParams = MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp2px(10f)
        }
        orientation = VERTICAL
        val pd10 = dp2px(10f)
        setPadding(pd10, dp2px(5f), pd10, dp2px(5f))
        background = getDrawable(context, R.color.rabbit_white)
    }

    override fun bindData(blockInfo: RabbitBlockStackTraceInfo, position: Int) {
        mRabbitBlockFrameViewTvStackTrace.text = blockInfo.stackTrace
    }

}