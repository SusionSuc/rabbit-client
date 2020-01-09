package com.susion.rabbit.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.susion.rabbit.base.common.rabbitSimpleTimeFormat
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.base.ui.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.entities.RabbitFpsAnalyzerInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitUiBlockDetailPage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_view_fps_analyzer.view.*
import kotlinx.android.synthetic.main.rabbit_view_ui_block_item.view.*

/**
 * susionwang at 2019-09-25
 */
class RabbitFpsAnalyzerView(context: Context) : RelativeLayout(context),
    RabbitAdapterItemView<RabbitFpsAnalyzerInfo> {

    init {
        LayoutInflater.from(context).inflate(R.layout.rabbit_view_fps_analyzer, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f))
    }

    @SuppressLint("SetTextI18n")
    override fun bindData(fpsInfo: RabbitFpsAnalyzerInfo, position: Int) {
        mFpsAnalyzerViewTvPageName.text = "${dropPackageName(fpsInfo.pageName)} -> ${fpsInfo.fpsCount} record "
        mFpsAnalyzerViewTvPageFps.text = "min -> ${fpsInfo.minFps}  avg -> ${fpsInfo.avgFps}  max -> ${fpsInfo.maxFps}"
    }

    private fun dropPackageName(str: String): String {
        val strSlice = str.split(".")
        if (strSlice.size < 3) return str
        return "${strSlice[strSlice.size - 2]}.${strSlice[strSlice.size - 1]}"
    }
}