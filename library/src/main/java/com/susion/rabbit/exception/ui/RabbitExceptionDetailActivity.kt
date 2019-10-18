package com.susion.rabbit.exception.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.susion.rabbit.R
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.exception.entities.RabbitExceptionInfo
import com.susion.rabbit.utils.showToast
import kotlinx.android.synthetic.main.activity_exception_detail.*

class RabbitExceptionDetailActivity : RabbitBaseActivity() {

    companion object {
        private val LOG_INFO = "exceptionInfo"
        fun start(context: Context, logInfo: RabbitExceptionInfo) {
            val intent = Intent(context, RabbitExceptionDetailActivity::class.java)
            intent.putExtra(LOG_INFO, logInfo)
            context.startActivity(intent)
        }
    }

    private val exceptionInfo by lazy {
        intent.getSerializableExtra(LOG_INFO) as? RabbitExceptionInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exception_detail)
        setBackListener(mExceptionDetailActionBar)
        mExceptionDetailActionBar.setTitle("异常详情")

        if (exceptionInfo == null || exceptionInfo?.isValid() == false) {
            showToast(this, "log日志文件已损坏!")
            return
        }

        renderExceptionInfo()
    }

    private fun renderExceptionInfo() {

        mExceptionDetailTvThreadName.text = "Crash Thread Name : ${exceptionInfo?.threadName ?:""}"
        mExceptionDetailTvFilePath.text = "File Path : ${exceptionInfo?.filePath}"

        val redMaxLine = 3
        val spannableStringBuilder = SpannableStringBuilder()
        var spanEndIndex = 0

        exceptionInfo?.crashTraceStr?.split("\n")?.forEachIndexed { index,string ->
            spannableStringBuilder.append(string)
            if (index < redMaxLine){
                spanEndIndex = spannableStringBuilder.length
            }
        }

        val foregroundColorSpan = ForegroundColorSpan(Color.RED)
        spannableStringBuilder.setSpan(foregroundColorSpan,0, spanEndIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        mExceptionDetailTvExceptionInfo.text = spannableStringBuilder
    }

}
