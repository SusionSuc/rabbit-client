package com.susion.rabbit.exception.ui

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.susion.rabbit.R
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.exception.RabbitExceptionLogStorageManager
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.exception.ui.view.ExceptionLogPreviewItemView
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import com.susion.rabbit.utils.RabbitUiUtils
import com.susion.rabbit.utils.showToast
import kotlinx.android.synthetic.main.activity_exception_detail.*
import kotlinx.android.synthetic.main.activity_exception_detail.view.*
import kotlinx.android.synthetic.main.activity_exception_list.*
import kotlinx.android.synthetic.main.activity_exception_list.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionDetailPage(context: Context): RabbitBasePage(context) {

    override fun getLayoutResId()  = R.layout.activity_exception_detail

    init {
        setTitle("异常详情")
    }
    override fun setEntryParams(exceptionInfo: Any) {
        if (exceptionInfo !is RabbitExceptionInfo) return

        if (!exceptionInfo.isvalid()) {
            showToast(context, "log日志文件已损坏!")
            return
        }

        mExceptionDetailTvThreadName.text = "Crash Thread Name : ${exceptionInfo?.threadName ?:""}"
//        mExceptionDetailTvFilePath.text = "File Path : ${exceptionInfo?.filePath}"

        val redMaxLine = 3
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append("长按复制 : ")

        var spanEndIndex = 0

        exceptionInfo.crashTraceStr.split("\n").forEachIndexed { index, string ->
            spannableStringBuilder.append(string)
            if (index < redMaxLine){
                spanEndIndex = spannableStringBuilder.length
            }
        }

        val foregroundColorSpan = ForegroundColorSpan(Color.RED)
        spannableStringBuilder.setSpan(foregroundColorSpan,0, spanEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        mExceptionDetailTvExceptionInfo.text = spannableStringBuilder

        mExceptionDetailTvExceptionInfo.setOnLongClickListener {
            RabbitUiUtils.copyStrToClipBoard(context, mExceptionDetailTvExceptionInfo.text.toString())
            true
        }
    }

}