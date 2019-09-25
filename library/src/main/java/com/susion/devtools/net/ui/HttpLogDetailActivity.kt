package com.susion.devtools.net.ui

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsBaseActivity
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.utils.showToast
import kotlinx.android.synthetic.main.activity_http_log_detail.*
import java.text.SimpleDateFormat
import java.util.*

class HttpLogDetailActivity : DevToolsBaseActivity() {

    companion object{
        private val LOG_INFO = "log_info"
        fun start(context: Context, logInfo: HttpLogInfo){
            val intent = Intent(context, HttpLogDetailActivity::class.java)
            intent.putExtra(LOG_INFO, logInfo)
            context.startActivity(intent)
        }
    }

    private val logInfo by lazy {
        intent.getSerializableExtra(LOG_INFO) as? HttpLogInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_http_log_detail)
        setBackListener(mHttpLogDetailActionBar)
        mHttpLogDetailActionBar.setTitle("日志详情")
        if (logInfo == null || logInfo?.isValid() == false){
            showToast(this, "log日志文件已损坏!")
            return
        }

        mHttpLogDetailTvRequestPath.text = "${logInfo?.path?:""}   ${getTime(logInfo?.time?:0L)}"
        mHttpLogDetailJsonView.bindJson(logInfo!!.responseStr)
    }

    private fun getTime(time:Long):String{
        return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
    }

}
