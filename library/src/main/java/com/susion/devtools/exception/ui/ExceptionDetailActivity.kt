package com.susion.devtools.exception.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.exception.entities.ExceptionInfo
import com.susion.devtools.utils.showToast
import kotlinx.android.synthetic.main.activity_exception_detail.*

class ExceptionDetailActivity : DevToolsBaseActivity() {

    companion object {
        private val LOG_INFO = "exceptionInfo"
        fun start(context: Context, logInfo: ExceptionInfo) {
            val intent = Intent(context, ExceptionDetailActivity::class.java)
            intent.putExtra(LOG_INFO, logInfo)
            context.startActivity(intent)
        }
    }

    private val exceptionInfo by lazy {
        intent.getSerializableExtra(LOG_INFO) as? ExceptionInfo
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

    //更容易发现是自己的应用的问题
    private fun renderExceptionInfo() {
        val spannableStringBuilder = SpannableStringBuilder(exceptionInfo?.crashTraceStr?:"")

//        val myPackageReg = "${this.application.packageName}"

        mExceptionDetailTvExceptionInfo.text = spannableStringBuilder
    }
}
