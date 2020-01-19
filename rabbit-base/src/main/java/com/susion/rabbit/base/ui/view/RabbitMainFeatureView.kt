package com.susion.rabbit.base.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.susion.lifeclean.common.recyclerview.AdapterItemView
import com.susion.rabbit.base.R
import com.susion.rabbit.base.config.RabbitMainFeatureInfo
import com.susion.rabbit.base.ui.RabbitUiKernal
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_main_feature_view.view.*

/**
 * susionwang at 2019-10-12
 */

class RabbitMainFeatureView : LinearLayout, AdapterItemView<RabbitMainFeatureInfo> {

    var clickListener: ClickListener? = null
    lateinit var mFeatureInfo: RabbitMainFeatureInfo

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_main_feature_view, this)
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL
        setPadding(dp2px(13f), dp2px(20f), dp2px(10f), dp2px(20f))
        background = getDrawable(context, R.color.rabbit_bg_card)
        throttleFirstClick(Consumer {
            clickListener?.onClick()
            RabbitUiKernal.openPage(mFeatureInfo.pageClass)
            if (mFeatureInfo.pageClass == null) {
                mFeatureInfo.action()
            }
        })
    }

    override fun bindData(fearureInfo: RabbitMainFeatureInfo, position: Int) {
        mFeatureInfo = fearureInfo
        mDevToolsMainFeatureTvName.text = fearureInfo.name
        mDevToolsMainFeatureIvIcon.setImageDrawable(
            getDrawable(
                context,
                fearureInfo.icon
            )
        )
    }

    interface ClickListener {
        fun onClick()
    }

}