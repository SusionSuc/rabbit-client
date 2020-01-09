package com.susion.rabbit.ui.page

import android.content.Context
import android.view.View
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.showToast
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_http_log_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-10-21
 */

class RabbitHttpLogDetailPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_http_log_detail

    init {
        setTitle("日志详情")
    }

    override fun setEntryParams(logInfo: Any){

        if (logInfo !is RabbitHttpLogInfo) return

        if (!logInfo.isvalid()){
            showToast(context, "log日志文件已损坏!")
            return
        }

        RabbitLog.d(TAG_MONITOR_UI,"enter RabbitHttpLogDetailPage show")

        mHttpLogDetailTvRequestPath.text = "${logInfo?.path?:""}   ${getTime(logInfo?.time?:0L)}"

        if (logInfo.requestParamsMapString.isNotEmpty()){
            mHttpLogDetailTvRequestParams.visibility = View.VISIBLE
            mHttpLogDetailTvRequestParams.text = logInfo.requestParamsMapString
        }else{
            mHttpLogDetailTvRequestParams.visibility = View.GONE
        }

        if (logInfo.requestBody.isNotEmpty()){
            mHttpLogDetailTvRequestBody.visibility = View.VISIBLE
            mHttpLogDetailTvRequestBody.text = logInfo.requestBody
        }else{
            mHttpLogDetailTvRequestBody.visibility = View.GONE
        }

        if (logInfo.isExceptionRequest){
            mHttpLogDetailExceptionInfoStr.visibility = View.VISIBLE
            mHttpLogDetailJsonView.visibility = View.GONE
            mHttpLogDetailExceptionInfoStr.text = logInfo.responseStr
        }else{
            mHttpLogDetailExceptionInfoStr.visibility = View.GONE
            mHttpLogDetailJsonView.visibility = View.VISIBLE
            if (logInfo.responseStr.isNotEmpty()){
                mHttpLogDetailJsonView.bindJson(logInfo.responseStr)
            }
        }
    }

    private fun getTime(time:Long):String{
        return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
    }

}