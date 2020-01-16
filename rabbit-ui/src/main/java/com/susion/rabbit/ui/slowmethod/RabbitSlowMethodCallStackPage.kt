package com.susion.rabbit.ui.slowmethod

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.entities.RabbitSlowMethodUiInfo
import kotlinx.android.synthetic.main.rabbit_page_call_stack.view.*

/**
 * susionwang at 2020-01-06
 */
class RabbitSlowMethodCallStackPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_call_stack

    init {
        setTitle("函数调用栈")
    }

    override fun setEntryParams(slowMethodInfo: Any) {

        if (slowMethodInfo !is RabbitSlowMethodUiInfo) return

        mCallStackThreadName.text = "${slowMethodInfo.className} -> ${slowMethodInfo.name}"

        val redMaxLine = 4
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append("长按复制 : ")

        var spanEndIndex = 0

        slowMethodInfo.callStack.split("\n").forEachIndexed { index, string ->
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