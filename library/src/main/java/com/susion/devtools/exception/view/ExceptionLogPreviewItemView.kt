package com.susion.devtools.exception.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.susion.devtools.R
import com.susion.devtools.base.adapter.AdapterItemView
import com.susion.devtools.exception.entities.ExceptionInfo
import com.susion.devtools.exception.ui.ExceptionDetailActivity
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.net.ui.HttpLogDetailActivity
import com.susion.devtools.utils.*
import com.susion.devtools.utils.dp2px
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_exception_log_pre_view_item.view.*
import kotlinx.android.synthetic.main.devtools_view_http_log_pre_view_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-09-25
 */
class ExceptionLogPreviewItemView(context: Context) : RelativeLayout(context), AdapterItemView<ExceptionInfo> {

    private lateinit var mLogInfo: ExceptionInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_exception_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            ExceptionDetailActivity.start(context, mLogInfo)
        })
    }

    override fun bindData(info: ExceptionInfo, position: Int) {
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