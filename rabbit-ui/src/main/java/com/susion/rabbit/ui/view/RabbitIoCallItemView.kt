package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.entities.RabbitIoCallInfo
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_io_call_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitIoCallItemView(context: Context) : RelativeLayout(context),
    AdapterItemView<RabbitIoCallInfo> {

    var eventListener:EventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_io_call_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(info: RabbitIoCallInfo, position: Int) {

        mRabbitIoCallItemLine1.text = "${RabbitUiUtils.dropPackageName(info.invokeStr)} ->"

        mRabbitIoCallItemLine2.text = RabbitUiUtils.dropPackageName(info.becalledStr)

        throttleFirstClick(Consumer {
            eventListener?.onClick("${info.invokeStr} -> ${info.becalledStr}")
        })
    }


    interface EventListener {
        fun onClick(str: String)
    }
}