package com.susion.rabbit.tracer.ui

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.susion.rabbit.R
import com.susion.rabbit.tracer.entities.RabbitPageSpeedInfo
import com.susion.rabbit.tracer.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_page_speed_detail.view.*
import java.util.ArrayList


/**
 * susionwang at 2019-10-29
 */
class RabbitPageSpeedDetailPage(context: Context) : RabbitBasePage(context) {


    override fun getLayoutResId() = R.layout.rabbit_page_page_speed_detail

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("页面测速详情")
        mRabbitSpeedDetailChart.apply {
            setBackgroundColor(Color.parseColor("#90caf9"))
            setTouchEnabled(true)
            description.isEnabled = false
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }
    }

    override fun setEntryParams(blockInfo: Any) {
        if (blockInfo !is RabbitPageSpeedUiInfo) return

        mRabbitSpeedDetailTvPageName.text = blockInfo.pageName.split(".").lastOrNull() ?: ""

        val count = blockInfo.speedInfoList.size
        var totalCreateTime = 0L
        var totalRenderTime = 0L

        blockInfo.speedInfoList.forEach {
            totalCreateTime += it.pageCreateTime
            totalRenderTime += it.pageRenderTime
        }

        mRabbitSpeedDetailTvCreateTime.text = "${(totalCreateTime / count).toInt()} ms"
        mRabbitSpeedDetailTvRenderTime.text = "${(totalRenderTime / count).toInt()} ms"

        renderChart(blockInfo.speedInfoList)
    }

    private fun renderChart(speedInfoList: ArrayList<RabbitPageSpeedInfo>) {

        val createTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            createTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.pageCreateTime.toFloat()))
        }

        val renderTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            renderTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.pageRenderTime.toFloat()))
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(LineDataSet(createTimes, "创建耗时"))
        dataSets.add(LineDataSet(renderTimes, "渲染耗时"))

        val createData = LineData(dataSets)

        mRabbitSpeedDetailChart.data = createData
    }

}