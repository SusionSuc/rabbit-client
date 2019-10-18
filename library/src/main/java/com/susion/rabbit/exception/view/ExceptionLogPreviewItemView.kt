package com.susion.rabbit.exception.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.exception.ui.RabbitExceptionDetailActivity
import com.susion.rabbit.utils.*
import com.susion.rabbit.utils.dp2px
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_exception_log_pre_view_item.view.*

/**
 * susionwang at 2019-09-25
 */
class ExceptionLogPreviewItemView(context: Context) : RelativeLayout(context), RabbitAdapterItemView<RabbitExceptionInfo> {

    private lateinit var mLogInfo: RabbitExceptionInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_exception_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            RabbitExceptionDetailActivity.start(context, mLogInfo)
        })
    }

    override fun bindData(info: RabbitExceptionInfo, position: Int) {
        mLogInfo = info
        mExceptionPreviewIv.setImageDrawable(getDrawable(context, R.drawable.devtools_icon_exception))

        val simpleExceptionName =info.exceptionName.substring( info.exceptionName.lastIndexOf('.')+1, info.exceptionName.length)
        mExceptionPreviewTvLine1.text = "${simpleExceptionName}  ${devToolsTimeFormat(info.time)}"
        if (info.simpleMessage.isNotEmpty()){
            mExceptionPreviewTvLine2.visibility = View.VISIBLE
            mExceptionPreviewTvLine2.text = info.simpleMessage
        }else{
            mExceptionPreviewTvLine2.visibility = View.GONE
        }
    }
}