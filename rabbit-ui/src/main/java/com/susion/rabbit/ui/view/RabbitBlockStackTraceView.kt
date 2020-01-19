package com.susion.rabbit.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.entities.RabbitBlockStackTraceInfo
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_view_block_frame_item.view.*
import java.lang.StringBuilder

/**
 * susionwang at 2019-09-25
 */

class RabbitBlockStackTraceView(context: Context) : LinearLayout(context),AdapterItemView<RabbitBlockStackTraceInfo> {

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
        setPadding(pd10,
            dp2px(5f), pd10,
            dp2px(5f)
        )
        background = getDrawable(context, R.color.rabbit_white)
    }

    override fun bindData(blockInfo: RabbitBlockStackTraceInfo, position: Int) {

        val realShowTrace = getRealShowTrace(blockInfo.stackTrace)

        mRabbitBlockFrameViewTvStackTrace.text = " 卡顿 ${blockInfo.collectCount}次  \n $realShowTrace"
    }

    //显示前30行
    private fun getRealShowTrace(stackTrace: String): String {

        val allTraceLine = stackTrace.split('\n')

        if (allTraceLine.size < 30) return stackTrace

        val realShowTrace = StringBuilder()

        allTraceLine.subList(0,30).forEach {
            realShowTrace.append(it)
            realShowTrace.append('\n')
        }

        return "$realShowTrace \n ..."
    }

}