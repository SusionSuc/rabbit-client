package com.susion.devtools.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.susion.devtools.R
import com.susion.devtools.base.DevToolsMainFeatureInfo
import com.susion.devtools.base.adapter.AdapterItemView
import com.susion.devtools.utils.dp2px
import com.susion.devtools.utils.getDrawable
import com.susion.devtools.utils.simpleStartActivity
import com.susion.devtools.utils.throttleFirstClick
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.devtools_view_main_feature_view.view.*

/**
 * susionwang at 2019-10-12
 */

class DevToolsMainFeatureView : RelativeLayout, AdapterItemView<DevToolsMainFeatureInfo> {

    lateinit var mFeatureInfo: DevToolsMainFeatureInfo

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
            if (mFeatureInfo.activityClass != null) {
                context.simpleStartActivity(mFeatureInfo.activityClass!!)
            }
        })
    }

    override fun bindData(fearureInfo: DevToolsMainFeatureInfo, position: Int) {
        mFeatureInfo = fearureInfo
        mDevToolsMainFeatureTvName.text = fearureInfo.name
        mDevToolsMainFeatureIvIcon.setImageDrawable(getDrawable(context, fearureInfo.icon))
    }
}