package com.susion.rabbit.ui.monitor.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.ui.R
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.monitor.page.RabbitExceptionDetailPage
import com.susion.rabbit.ui.base.dp2px
import com.susion.rabbit.ui.base.getDrawable
import com.susion.rabbit.ui.base.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_exception_log_pre_view_item.view.*

/**
 * susionwang at 2019-09-25
 */
class ExceptionLogPreviewItemView(context: Context) : RelativeLayout(context),
    com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView<RabbitExceptionInfo> {

    private lateinit var mLogInfo: RabbitExceptionInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_exception_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            dp2px(50f)
        ).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitExceptionDetailPage::class.java, mLogInfo)
        })
    }

    override fun bindData(info: RabbitExceptionInfo, position: Int) {
        mLogInfo = info
        mExceptionPreviewIv.setImageDrawable(
            getDrawable(
                context,
                R.drawable.rabbit_icon_exception
            )
        )

        val simpleExceptionName =info.exceptionName.substring( info.exceptionName.lastIndexOf('.')+1, info.exceptionName.length)
        mExceptionPreviewTvLine1.text = "${simpleExceptionName}  ${rabbitTimeFormat(
            info.time
        )}"
        if (info.simpleMessage.isNotEmpty()){
            mExceptionPreviewTvLine2.visibility = View.VISIBLE
            mExceptionPreviewTvLine2.text = info.simpleMessage
        }else{
            mExceptionPreviewTvLine2.visibility = View.GONE
        }
    }
}