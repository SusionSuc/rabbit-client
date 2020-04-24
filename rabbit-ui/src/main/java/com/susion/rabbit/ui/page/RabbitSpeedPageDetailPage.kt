package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.entities.RabbitApiInfo
import com.susion.rabbit.base.entities.RabbitPageApiInfo
import com.susion.rabbit.base.entities.RabbitPageSpeedInfo
import com.susion.rabbit.base.entities.RabbitPageSpeedUiInfo
import com.susion.rabbit.base.greendao.RabbitPageSpeedInfoDao
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_page_speed_detail.view.*
import java.lang.Exception
import java.util.ArrayList

/**
 * susionwang at 2019-10-29
 */
class RabbitSpeedPageDetailPage(context: Context) : RabbitBasePage(context) {

    private var pageName: String = ""

    override fun getLayoutResId() = R.layout.rabbit_page_page_speed_detail

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("页面测速详情")
        initChart(mRabbitSpeedDetailChart)
        mRabbitSpeedDetailTvPageSRL.setOnRefreshListener {
            RabbitStorage.getAll(
                RabbitPageSpeedInfo::class.java,
                condition = Pair(RabbitPageSpeedInfoDao.Properties.PageName, pageName)
            ) {
                mRabbitSpeedDetailTvPageSRL.isRefreshing = false
                renderUi(it)
            }
        }
    }

    override fun setEntryParams(blockInfo: Any) {
        if (blockInfo !is RabbitPageSpeedUiInfo) return

        pageName = blockInfo.pageName

        mRabbitSpeedDetailTvPageName.text = blockInfo.pageName.split(".").lastOrNull() ?: ""

        renderUi(blockInfo.speedInfoList)
    }

    private fun renderUi(speedInfos: List<RabbitPageSpeedInfo>) {

        mRabbitSpeedDetailRequestChartLl.removeAllViews()

        mRabbitSpeedDetailTvCreateTime.text =
            "${(speedInfos.map { it.pageCreateTime }.average()).toInt()} ms"
        mRabbitSpeedDetailTvInlateTime.text =
            "${(speedInfos.map { it.pageInflateTime }.average()).toInt()} ms"
        mRabbitSpeedDetailTvFullRenderTime.text =
            "${(speedInfos.map { it.fullRenderTime }.average()).toInt()} ms"

        val orderSpeedInfos = speedInfos.sortedBy { it.time }

        renderChart(orderSpeedInfos)

        renderRequestChart(orderSpeedInfos)
    }

    private fun renderChart(speedInfoList: List<RabbitPageSpeedInfo>) {

        val createTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            createTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.pageCreateTime.toFloat()))
        }

        val inflateTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            inflateTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.pageInflateTime.toFloat()))
        }

        val renderTimes = ArrayList<Entry>()
        speedInfoList.forEachIndexed { index, rabbitPageSpeedInfo ->
            renderTimes.add(Entry(index.toFloat(), rabbitPageSpeedInfo.fullRenderTime.toFloat()))
        }

        val dataSets = ArrayList<ILineDataSet>()
        val onCreateDatas = LineDataSet(createTimes, "onCreate耗时").apply {
            setCircleColor(Color.parseColor("#00e676"))
            color = Color.parseColor("#00e676")
        }

        val inflateDatas = LineDataSet(inflateTimes, "inflate耗时").apply {
            setCircleColor(Color.parseColor("#ff4081"))
            color = Color.parseColor("#ff4081")
        }

        val fullDrawDatas = LineDataSet(renderTimes, "完全渲染耗时").apply {
            setCircleColor(Color.parseColor("#aa00ff"))
            color = Color.parseColor("#aa00ff")
        }

        dataSets.add(onCreateDatas)
        dataSets.add(inflateDatas)
        dataSets.add(fullDrawDatas)

        mRabbitSpeedDetailChart.data = LineData(dataSets)
        mRabbitSpeedDetailChart.invalidate()

    }

    private fun renderRequestChart(speedInfoList: List<RabbitPageSpeedInfo>) {

        val allRequestCostInfo = LinkedHashMap<String, List<RabbitApiInfo>>()

        speedInfoList.forEach {
            val costInfo = getRequestCostInfo(it.apiRequestCostString)
            costInfo?.apiStatusList?.forEach { apiInfo ->
                var infoList = allRequestCostInfo[apiInfo.api]
                if (infoList == null) {
                    infoList = ArrayList()
                    allRequestCostInfo[apiInfo.api] = infoList
                }
                (infoList as ArrayList).add(apiInfo)
            }
        }

        allRequestCostInfo.forEach {
            mRabbitSpeedDetailRequestChartLl.addView(getRequestChartView(it))
        }

    }

    private fun getRequestChartView(apiInfoList: Map.Entry<String, List<RabbitApiInfo>>): View {

        val chart = LineChart(context)
        chart.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(200f)).apply {
            bottomMargin = dp2px(20f)
        }
        initChart(chart)

        val dataSets = ArrayList<ILineDataSet>()

        val costTimes = ArrayList<Entry>()
        apiInfoList.value.forEachIndexed { index, costInfo ->
            costTimes.add(Entry(index.toFloat(), costInfo.costTime.toFloat()))
        }

        val onCreateDatas = LineDataSet(costTimes, apiInfoList.key).apply {
            setCircleColor(Color.parseColor("#00e676"))
            color = Color.parseColor("#00e676")
        }

        dataSets.add(onCreateDatas)

        chart.data = LineData(dataSets)
        chart.invalidate()
        return chart
    }

    private fun getRequestCostInfo(apiRequestCostString: String?): RabbitPageApiInfo? {
        if (apiRequestCostString.isNullOrEmpty()) return null
        try {
            return Gson().fromJson(apiRequestCostString, RabbitPageApiInfo::class.java)
        } catch (e: Exception) {
            RabbitLog.d(TAG_MONITOR_UI,"getRequestCostInfo error : ${e.message}")
        }
        return null
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