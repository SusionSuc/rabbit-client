package com.susion.rabbit.base.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.utils.dp2px
import com.susion.rabbit.utils.getDrawable
import com.susion.rabbit.utils.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.dev_tools_tool_bar.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitActionBar : RelativeLayout {

    var actionListener: ActionListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.dev_tools_tool_bar, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(45f))
        background = getDrawable(context, R.drawable.rabbit_action_bar_coner_rect)
        mDevToolsToolsBarIvBack.throttleFirstClick(Consumer {
            actionListener?.onBackClick()
        })

        mRabbitActionBarFakeEt.inputType = InputType.TYPE_NULL
        mRabbitActionBarFakeEt.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
                actionListener?.onBackClick()
            }
            return@OnKeyListener true
        })

        mRabbitActionBarQuickHider.throttleFirstClick(Consumer {
            Rabbit.uiManager.handleFloatingViewClickEvent()
        })
    }

    fun setTitle(title: String) {
        mDevToolsToolsBarTvTitle.text = title
    }

    interface ActionListener {
        fun onBackClick()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            mRabbitActionBarFakeEt.requestFocus()
        }
    }
}