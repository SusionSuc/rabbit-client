package com.susion.devtools.net.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.susion.devtools.R
import com.susion.devtools.base.adapter.AdapterItemView
import com.susion.devtools.net.entities.HttpLogInfo
import com.susion.devtools.net.ui.HttpLogDetailActivity
import com.susion.devtools.utils.DevToolsUiUtils
import com.susion.devtools.utils.dp2px
import com.susion.devtools.utils.getDrawable
import com.susion.devtools.utils.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_http_log_pre_view_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * susionwang at 2019-09-25
 */
class HttpLogPreviewItemView(context: Context) : RelativeLayout(context), AdapterItemView<HttpLogInfo> {

    private lateinit var mLogInfo: HttpLogInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_http_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            HttpLogDetailActivity.start(context, mLogInfo)
        })
    }

    override fun bindData(logInfo: HttpLogInfo, position: Int) {
        mLogInfo = logInfo
        mLogPreViewTvHost.text = "${logInfo.host}    ${logInfo.requestType}    ${getTime(logInfo.time)} "
        val res = when(logInfo.responseContentType){
            HttpLogInfo.ResponseContentType.GSON -> R.drawable.devtools_icon_type_json
            else -> R.drawable.devtools_icon_type_json
        }
        mLogPreViewIvResponseContentType.setImageDrawable(getDrawable(context, res))

        if (logInfo.isSuccessRequest){
            mLogPreViewTvPath.text = "${logInfo.path}"
            mLogPreViewTvPath.setTextColor(DevToolsUiUtils.getColor(context,R.color.devtools_black))
            mLogPreViewTvHost.setTextColor(DevToolsUiUtils.getColor(context,R.color.devtools_black))
        }else{
            mLogPreViewTvPath.text = "${logInfo.path} ${logInfo.responseCode}"
            mLogPreViewTvPath.setTextColor(DevToolsUiUtils.getColor(context,R.color.devtools_error_red))
            mLogPreViewTvHost.setTextColor(DevToolsUiUtils.getColor(context,R.color.devtools_error_red))
        }
    }

    private fun getTime(time:Long):String{
        return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
    }

}