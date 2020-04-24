package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.susion.rabbit.base.entities.RabbitAppStartSpeedInfo
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_app_start_speed_detail.view.*
import java.util.*


/**
 * susionwang at 2019-10-29
 * 应用启动相关数据详情页
 */
class RabbitSpeedAppStartDetailPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_app_start_speed_detail

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        setTitle("应用启动测速详情")

        mRabbitAppSpeedPageChart.apply {
            setBackgroundColor(getColor(context, R.color.rabbit_bg_card))
            setTouchEnabled(true)
            description.isEnabled = false
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setPadding(
                dp2px(3f),
                dp2px(3f),
                dp2px(3f),
                dp2px(3f)
            )
            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        RabbitStorage.getAll(RabbitAppStartSpeedInfo::class.java) { speedInfos ->

            val onCreateCostList =
                speedInfos.map { it.createEndTime - it.createStartTime }.filter { it != 0L }
            val fullCostList = speedInfos.map { it.fullShowCostTime }.filter { it != 0L }

            mRabbitAppSpeedPageTvAvgOnCreate.text = "${(onCreateCostList.average()).toInt()} ms"
            mRabbitAppSpeedPageTvAvgFullTime.text = "${(fullCostList.average()).toInt()} ms"

            val maxOnCreate = onCreateCostList.max()?.toInt() ?: 0
            val minOnCreate = onCreateCostList.min()?.toInt() ?: 0
            mRabbitAppSpeedPageTvRangeOnCreate.text = "($minOnCreate ~ $maxOnCreate) ms"

            val maxFull = fullCostList.max()?.toInt() ?: 0
            val minFull = fullCostList.min()?.toInt() ?: 0
            mRabbitAppSpeedPageTvRangeFullStart.text = "($minFull ~ $maxFull) ms"

            mRabbitAppSpeedPageTvLogNumber.text = speedInfos.size.toString()

            renderChart(speedInfos)
        }
    }

    private fun renderChart(speedInfoList: List<RabbitAppStartSpeedInfo>) {

        val createTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            createTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.appCreateCost().toFloat()))
        }

        val renderTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            renderTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.fullShowCostTime.toFloat()))
        }

        val dataSets = ArrayList<ILineDataSet>()
        val onCreateDatas = LineDataSet(createTimes, "onCreate耗时").apply {
            setCircleColor(Color.parseColor("#00e676"))
            color = Color.parseColor("#00e676")
        }

        val coldDatas = LineDataSet(renderTimes, "冷启动耗时").apply {
            setCircleColor(Color.parseColor("#ff4081"))
            color = Color.parseColor("#ff4081")
        }

        dataSets.add(onCreateDatas)
        dataSets.add(coldDatas)

        mRabbitAppSpeedPageChart.data = LineData(dataSets)
        mRabbitAppSpeedPageChart.invalidate()
    }


}