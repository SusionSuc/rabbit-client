package com.susion.rabbit.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.rabbit.R
import com.susion.rabbit.Rabbit
import com.susion.rabbit.base.RabbitMainFeatureInfo
import com.susion.rabbit.base.adapter.RabbitAdapterItemView
import com.susion.rabbit.utils.dp2px
import com.susion.rabbit.utils.getDrawable
import com.susion.rabbit.utils.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_main_feature_view.view.*

/**
 * susionwang at 2019-10-12
 */

class RabbitMainFeatureView : RelativeLayout, RabbitAdapterItemView<RabbitMainFeatureInfo> {

    lateinit var mFeatureInfo: RabbitMainFeatureInfo

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.devtools_view_main_feature_view, this)
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f)).apply {
            topMargin = dp2px(10f)
        }
        val pd10 = dp2px(10f)
        setPadding(pd10, 0, 0, 0)
        throttleFirstClick(Consumer {
            Rabbit.uiManager.openPage(mFeatureInfo.pageClass)
        })
    }

    override fun bindData(fearureInfo: RabbitMainFeatureInfo, position: Int) {
        mFeatureInfo = fearureInfo
        mDevToolsMainFeatureTvName.text = fearureInfo.name
        mDevToolsMainFeatureIvIcon.setImageDrawable(getDrawable(context, fearureInfo.icon))
    }

}