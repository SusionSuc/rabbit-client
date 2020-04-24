package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.dp2px
import com.susion.rabbit.base.ui.getColor
import com.susion.rabbit.base.ui.throttleFirstClick
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.monitor.R
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.rabbit_page_memory_compose.view.*

/**
 * susionwang at 2019-12-03
 * 内存分析
 */
class RabbitMemoryGlobalAnalyzerPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_memory_compose

    init {

        setTitle("内存概览-全局")

        initChart(mRabbitMemComposePageMemChart)

        mRabbitMemComposePageTvMaxMem.text =
            RabbitUiUtils.formatFileSize(Runtime.getRuntime().maxMemory())

        mRabbitMemComposePageSRL.setOnRefreshListener {
            loadData()
        }

        mRabbitMemAnalyzerPageTvPageDetail.throttleFirstClick(Consumer {
            RabbitUi.openPage(RabbitMemoryPageAnalyzerPage::class.java)
        })

        loadData()

    }

    private fun loadData() {
        RabbitStorage.getAll(
            RabbitMemoryInfo::class.java,
            loadResult = { memInfos ->

                mRabbitMemComposePageSRL.isRefreshing = false

                val avgMem = memInfos.map { it.totalSize }.average()
                val avgVmMem = memInfos.map { it.vmSize }.average()

                mRabbitMemComposePageTvAvgMem.text = RabbitUiUtils.formatFileSize(avgMem.toLong())
                mRabbitMemComposePageTvAvgHeapMem.text =
                    RabbitUiUtils.formatFileSize(avgVmMem.toLong())

                val totalSizes = ArrayList<Entry>()
                memInfos.forEachIndexed { index, memInfo ->
                    totalSizes.add(Entry(index.toFloat(), formatSizeToMB(memInfo.totalSize)))
                }

                RabbitLog.d(TAG_MONITOR_UI, "total calculate memory count : ${totalSizes.size}")

                val vmSizes = ArrayList<Entry>()
                memInfos.forEachIndexed { index, memInfo ->
                    vmSizes.add(Entry(index.toFloat(), formatSizeToMB(memInfo.vmSize)))
                }

                val nativeSizes = ArrayList<Entry>()
                memInfos.forEachIndexed { index, memInfo ->
                    nativeSizes.add(Entry(index.toFloat(), formatSizeToMB(memInfo.nativeSize)))
                }

                val dataSets = ArrayList<ILineDataSet>()
                val totalMemDatas = LineDataSet(totalSizes, "total mem").apply {
                    color = Color.parseColor("#00e676")
                    setDrawCircles(false)
                }

                val vmMemDatas = LineDataSet(vmSizes, "vm mem").apply {
                    color = Color.parseColor("#ff4081")
                    setDrawCircles(false)
                }

                val nativeMemDatas = LineDataSet(nativeSizes, "native mem").apply {
                    color = Color.parseColor("#aa00ff")
                    setDrawCircles(false)
                }

                dataSets.add(totalMemDatas)
                dataSets.add(vmMemDatas)
                dataSets.add(nativeMemDatas)

                mRabbitMemComposePageMemChart.data = LineData(dataSets)

                mRabbitMemComposePageMemChart.invalidate()
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

    private fun formatSizeToMB(size: Int): Float {
        return size / 1024f / 1024f
    }

}