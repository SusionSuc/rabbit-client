package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.susion.rabbit.base.common.RabbitUtils
import com.susion.rabbit.base.common.rabbitSimpleTimeFormat
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.ui.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.entities.RabbitFpsAnalyzerInfo
import com.susion.rabbit.ui.entities.RabbitMemoryAnalyzerPageInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitFpsAnalyzerDetailPage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_fps_analyzer.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitMemPageInfoView(context: Context) : RelativeLayout(context),
    RabbitAdapterItemView<RabbitMemoryAnalyzerPageInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_fps_analyzer, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f))
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(memInfo: RabbitMemoryAnalyzerPageInfo, position: Int) {
        mFpsAnalyzerViewTvPageName.text =
            "${RabbitUtils.dropPackageName(memInfo.pageName)} -> ${memInfo.recordCount} record "
        mFpsAnalyzerViewTvPageFps.text =
            "avg total mem -> ${memInfo.avgMem}  avg vm meme-> ${memInfo.avgVmMem} "

    }

}