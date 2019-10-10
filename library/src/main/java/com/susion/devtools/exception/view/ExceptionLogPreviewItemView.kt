package com.susion.devtools.exception.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.susion.devtools.R
import com.susion.devtools.base.adapter.AdapterItemView
import com.susion.devtools.exception.entities.ExceptionInfo
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
class ExceptionLogPreviewItemView(context: Context) : RelativeLayout(context), AdapterItemView<ExceptionInfo> {

    private lateinit var mLogInfo: HttpLogInfo

    init {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_exception_log_pre_view_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            bottomMargin = dp2px(5f)
        }
        throttleFirstClick(Consumer {
            HttpLogDetailActivity.start(context, mLogInfo)
        })
    }

    override fun bindData(exceptionInfo: ExceptionInfo, position: Int) {

    }

    private fun getTime(time:Long):String{
        return SimpleDateFormat("MM/dd HH:mm:ss").format(Date(time))
    }

}