package com.susion.rabbit.base.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.RelativeLayout
import com.susion.rabbit.base.R
import com.susion.rabbit.base.ui.dp2px
import kotlinx.android.synthetic.main.rabbit_view_switch_btn.view.*

/**
 * susionwang at 2019-10-18
 */
class RabbitSwitchButton : RelativeLayout {

    var checkedStatusChangeListener: CheckedStatusChangeListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_switch_btn, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            dp2px(60f)
        )
        mRabbitSwitchBtnSc.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                checkedStatusChangeListener?.checkedStatusChange(isChecked)
            }
        })
    }

    fun refreshUi(name: String, status: Boolean) {
        mRabbitSwitchBtnTvDesc.text = name
        mRabbitSwitchBtnSc.isChecked = status
    }


    interface CheckedStatusChangeListener{
        fun checkedStatusChange(isChecked:Boolean)
    }

}