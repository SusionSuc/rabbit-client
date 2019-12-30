package com.susion.rabbit.ui.monitor.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.rabbit.ui.base.utils.RabbitUiUtils
import com.susion.rabbit.base.common.rabbitTimeFormat
import com.susion.rabbit.base.entities.RabbitHttpLogInfo
import com.susion.rabbit.ui.R
import com.susion.rabbit.ui.base.RabbitUi
import com.susion.rabbit.ui.monitor.page.RabbitHttpLogDetailPage
import com.susion.rabbit.ui.base.dp2px
import com.susion.rabbit.ui.base.getDrawable
import com.susion.rabbit.ui.base.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_http_log_pre_view_item.view.*

/**
 * susionwang at 2019-09-25
 */
class HttpLogPreviewItemView(context: Context) : RelativeLayout(context),
    com.susion.rabbit.ui.base.adapter.RabbitAdapterItemView<RabbitHttpLogInfo> {

    private lateinit var mLogInfo: RabbitHttpLogInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_http_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            dp2px(50f)
        ).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitHttpLogDetailPage::class.java, mLogInfo)
        })
    }

    override fun bindData(logInfo: RabbitHttpLogInfo, position: Int) {
        mLogInfo = logInfo
        mLogPreViewTvHost.text = "${logInfo.host}    ${logInfo.requestType}    ${rabbitTimeFormat(
            logInfo.time
        )} "
        val res = when(logInfo.responseContentType){
            "gson" -> R.drawable.rabbit_icon_type_json
            else -> R.drawable.rabbit_icon_type_json
        }
        mLogPreViewIvResponseContentType.setImageDrawable(
            getDrawable(
                context,
                res
            )
        )

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