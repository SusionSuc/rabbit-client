package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.base.greendao.RabbitFPSInfoDao
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.ui.entities.RabbitFpsAnalyzerInfo
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_fps_analyzer_detail.view.*

/**
 * susionwang at 2019-12-03
 */
class RabbitFpsAnalyzerDetailPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_fps_analyzer_detail

    override fun setEntryParams(fpsInfo: Any) {

        if (fpsInfo !is RabbitFpsAnalyzerInfo) return

        setTitle("${RabbitUiUtils.dropPackageName(fpsInfo.pageName)} FPS曲线")

        initChart(mRabbitFpsDetailChart)

        mRabbitFpsDetailSRL.setOnRefreshListener {
            loadData(fpsInfo.pageName)
        }

        loadData(fpsInfo.pageName)
    }

    private fun loadData(pageName: String) {

        RabbitStorage.getAll(
            RabbitFPSInfo::class.java,
            Pair(RabbitFPSInfoDao.Properties.PageName, pageName),
            loadResult = { fpses ->

                mRabbitFpsDetailSRL.isRefreshing = false

                mRabbitFpsDetailTvMinFps.text = fpses.map { it.minFps }.min()?.toInt().toString()

                mRabbitFpsDetailTvAvgFps.text = fpses.map { it.avgFps }.average().toInt().toString()

                mRabbitFpsDetailTvMaxFps.text = fpses.map { it.maxFps }.max()?.toInt().toString()

                val fpsEntryes = ArrayList<Entry>()
                fpses.forEachIndexed { index, fpsInfo ->
                    fpsEntryes.add(Entry(index.toFloat(), fpsInfo.avgFps.toFloat()))
                }

                val dataSets = ArrayList<ILineDataSet>()
                val fpsDatas = LineDataSet(fpsEntryes, "fps").apply {
                    color = Color.parseColor("#00e676")
                    setDrawCircles(false)
                }

                dataSets.add(fpsDatas)

                mRabbitFpsDetailChart.data = LineData(dataSets)

                mRabbitFpsDetailChart.invalidate()
            })
    }

    private fun initChart(chart: LineChart) {
        chart.apply {
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
    }
}