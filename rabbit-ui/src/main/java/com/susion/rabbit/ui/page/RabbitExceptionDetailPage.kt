package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.susion.rabbit.base.entities.RabbitExceptionInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.showToast
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_call_stack.view.*

/**
 * susionwang at 2019-10-21
 */

class RabbitExceptionDetailPage(context: Context): RabbitBasePage(context) {

    override fun getLayoutResId()  = R.layout.rabbit_page_call_stack

    init {
        setTitle("异常详情")
    }

    override fun setEntryParams(exceptionInfo: Any) {
        if (exceptionInfo !is RabbitExceptionInfo) return

        if (!exceptionInfo.isvalid()) {
            showToast(context, "log日志文件已损坏!")
            return
        }

        mCallStackThreadName.text = "Crash Thread Name : ${exceptionInfo?.threadName ?:""}"

        val redMaxLine = 4
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append("长按复制 : ")

        var spanEndIndex = 0

        exceptionInfo.crashTraceStr.split("\n").forEachIndexed { index, string ->
            spannableStringBuilder.append(string)
            spannableStringBuilder.append("\n")
            if (index < redMaxLine){
                spanEndIndex = spannableStringBuilder.length
            }
        }

        val foregroundColorSpan = ForegroundColorSpan(Color.RED)
        spannableStringBuilder.setSpan(foregroundColorSpan,0, spanEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        mCallStackTvStackStr.text = spannableStringBuilder

        mCallStackTvStackStr.setOnLongClickListener {
            RabbitUiUtils.copyStrToClipBoard(context, mCallStackTvStackStr.text.toString())
            true
        }
    }

}