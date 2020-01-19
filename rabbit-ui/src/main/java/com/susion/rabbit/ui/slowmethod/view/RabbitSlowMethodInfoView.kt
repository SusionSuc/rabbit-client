package com.susion.rabbit.ui.slowmethod.view

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.ui.*
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.entities.RabbitSlowMethodUiInfo
import com.susion.rabbit.ui.slowmethod.RabbitSlowMethodCallStackPage
import io.reactivex.functions.Consumer

/**
 * susionwang at 2020-01-02
 */
class RabbitSlowMethodInfoView (context: Context) : LinearLayout(context), AdapterItemView<RabbitSlowMethodUiInfo> {

    private val tvClassName = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        setTextColor(getColor(context, R.color.rabbit_black))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private val tvMethodDesc = TextView(context).apply {
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        setTextColor(getColor(context, R.color.rabbit_black))
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp2px(5f)
        }
    }

    init {
        orientation = VERTICAL
        layoutParams = MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            val mr10 = dp2px(10f)
            setMargins(mr10, mr10, mr10, 0)
        }
        addView(tvClassName)
        addView(tvMethodDesc)
        background = getDrawable(context, R.color.rabbit_bg_card)
        setPadding(
            dp2px(10f),
            dp2px(5f),
            dp2px(10f),
            dp2px(5f)
        )
    }


    override fun bindData(info: RabbitSlowMethodUiInfo, position: Int) {
        tvClassName.text= "${info.className} -> ${info.name}"
        tvMethodDesc.text = "total ${info.count} record ; average cost ${info.totalTime/info.count} ms"

        throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitSlowMethodCallStackPage::class.java, info)
        })
    }

}