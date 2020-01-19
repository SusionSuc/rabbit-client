package com.susion.rabbit.base.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.R
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.base.ui.getDrawable

/**
 * susionwang at 2019-10-12
 */

class RabbitSimpleKvInfo(val key: String, val value: String, val type: Int = 1)

class RabbitSimpleKVItemView : LinearLayout, AdapterItemView<RabbitSimpleKvInfo> {

    private val tvKey = TextView(context).apply {
        layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            weight = 0.4f
        }
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        setTextColor(getColor(context, R.color.rabbit_black))
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp2px(10f), 0, 0, 0)
        setLineSpacing(0f, 1.5f)
    }

    private val tvValue = TextView(context).apply {
        layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            weight = 0.6f
        }
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
        setTextColor(getColor(context, R.color.rabbit_black))
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp2px(10f), 0, 0, 0)
        setLineSpacing(0f, 1.5f)
    }

    private val dividerLine = View(context).apply {
        layoutParams = LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT)
        background = getDrawable(context, R.color.rabbit_divider_line)
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    override fun bindData(kvInfo: RabbitSimpleKvInfo, position: Int) {
        tvKey.text = kvInfo.key
        tvValue.text = kvInfo.value
        val textColor = when (kvInfo.type) {
            1 -> Color.BLACK
            2 -> Color.LTGRAY
            else -> Color.BLUE
        }
        tvKey.setTextColor(textColor)
        tvValue.setTextColor(textColor)
    }

    private fun initView() {
        addView(tvKey)
        addView(dividerLine)
        addView(tvValue)
        orientation = HORIZONTAL
        layoutParams = MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            dp2px(40f)
        )
    }

}