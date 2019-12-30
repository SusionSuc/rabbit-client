package com.susion.rabbit.ui.monitor.page

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.susion.rabbit.ui.base.utils.RabbitUiUtils
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.ui.R
import com.susion.rabbit.ui.base.showToast
import kotlinx.android.synthetic.main.rabbit_page_exception_detail.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionDetailPage(context: Context): com.susion.rabbit.ui.base.RabbitBasePage(context) {

    override fun getLayoutResId()  = R.layout.rabbit_page_exception_detail

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