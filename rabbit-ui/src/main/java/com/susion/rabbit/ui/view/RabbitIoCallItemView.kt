package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.susion.rabbit.base.entities.RabbitIoCallInfo
import com.susion.rabbit.base.ui.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_io_call_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitIoCallItemView(context: Context) : RelativeLayout(context),
    RabbitAdapterItemView<RabbitIoCallInfo> {

    var eventListener:EventListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_io_call_item, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(info: RabbitIoCallInfo, position: Int) {

        mRabbitIoCallItemLine1.text = "${dropPackageName(info.invokeStr)} ->"

        mRabbitIoCallItemLine2.text = dropPackageName(info.becalledStr)

        throttleFirstClick(Consumer {
            eventListener?.onClick("${info.invokeStr} -> ${info.becalledStr}")
        })
    }

    private fun dropPackageName(str: String): String {
        val strSlice = str.split(".")
        if (strSlice.size < 3) return str
        return "${strSlice[strSlice.size - 2]}.${strSlice[strSlice.size - 1]}"
    }


    interface EventListener {
        fun onClick(str: String)
    }
}