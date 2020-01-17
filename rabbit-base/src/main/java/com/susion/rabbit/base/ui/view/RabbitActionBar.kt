package com.susion.rabbit.base.ui.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.susion.rabbit.base.R
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_action_bar.view.*

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
        LayoutInflater.from(context).inflate(R.layout.rabbit_action_bar, this)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            dp2px(50f)
        )

        background = getDrawable(context, R.color.rabbit_material_primary)

        mDevToolsToolsBarIvBack.throttleFirstClick(Consumer {
            actionListener?.onBackClick()
        })

        mRabbitActionBarFakeEt.inputType = InputType.TYPE_NULL
        mRabbitActionBarFakeEt.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (event?.keyCode == KeyEvent.KEYCODE_BACK && RabbitUiKernal.pageIsShow()) {
                actionListener?.onBackClick()
                return@OnKeyListener true
            } else {
                return@OnKeyListener false
            }
        })
    }

    fun setTitle(title: String) {
        mDevToolsToolsBarTvTitle.text = title
    }

    fun setRightOperate(draRes: Int, clickCallback: () -> Unit) {
        mDevToolsToolsBarIvOperation.visibility = View.VISIBLE
        mDevToolsToolsBarIvOperation.setImageDrawable(getDrawable(context, draRes))
        mDevToolsToolsBarIvOperation.throttleFirstClick(Consumer {
            clickCallback()
        })
    }

    interface ActionListener {
        fun onBackClick()
    }

    //support back by back keyword
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postDelayed({
            if (!mRabbitActionBarFakeEt.hasFocus()) {
                mRabbitActionBarFakeEt.requestFocus()
            }
        },500)
    }

}