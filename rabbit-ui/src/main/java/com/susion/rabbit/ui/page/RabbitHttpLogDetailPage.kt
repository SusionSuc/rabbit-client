package com.susion.rabbit.ui.page

import android.content.Context
import android.view.View
import com.susion.rabbit.R
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.utils.showToast
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

        if (logInfo !is com.susion.rabbit.base.entities.RabbitHttpLogInfo) return

        if (!logInfo.isvalid()){
            showToast(context, "log日志文件已损坏!")
            return
        }

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

        if (logInfo.responseStr.isNotEmpty()){
            mHttpLogDetailJsonView.bindJson(logInfo.responseStr)
        }

    }

    private fun getTime(time:Long):String{
        return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
    }

}