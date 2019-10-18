package com.susion.rabbit.net.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import com.susion.rabbit.net.ui.RabbitHttpLogDetailActivity
import com.susion.rabbit.utils.*
import com.susion.rabbit.utils.dp2px
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_http_log_pre_view_item.view.*

/**
 * susionwang at 2019-09-25
 */
class HttpLogPreviewItemView(context: Context) : RelativeLayout(context), RabbitAdapterItemView<RabbitHttpLogInfo> {

    private lateinit var mLogInfo: RabbitHttpLogInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_http_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            RabbitHttpLogDetailActivity.start(context, mLogInfo)
        })
    }

    override fun bindData(logInfo: RabbitHttpLogInfo, position: Int) {
        mLogInfo = logInfo
        mLogPreViewTvHost.text = "${logInfo.host}    ${logInfo.requestType}    ${devToolsTimeFormat(logInfo.time)} "
        val res = when(logInfo.responseContentType){
            RabbitHttpLogInfo.ResponseContentType.GSON -> R.drawable.devtools_icon_type_json
            else -> R.drawable.devtools_icon_type_json
        }
        mLogPreViewIvResponseContentType.setImageDrawable(getDrawable(context, res))

        if (logInfo.isSuccessRequest){
            mLogPreViewTvPath.text = "${logInfo.path}"
            mLogPreViewTvPath.setTextColor(RabbitUiUtils.getColor(context,R.color.rabbit_black))
            mLogPreViewTvHost.setTextColor(RabbitUiUtils.getColor(context,R.color.rabbit_black))
        }else{
            mLogPreViewTvPath.text = "${logInfo.path} ${logInfo.responseCode}"
            mLogPreViewTvPath.setTextColor(RabbitUiUtils.getColor(context,R.color.rabbit_error_red))
            mLogPreViewTvHost.setTextColor(RabbitUiUtils.getColor(context,R.color.rabbit_error_red))
        }
    }

}