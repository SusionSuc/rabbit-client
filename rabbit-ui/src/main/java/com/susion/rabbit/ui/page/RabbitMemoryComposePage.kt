package com.susion.rabbit.ui.page

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.susion.rabbit.R
import com.susion.rabbit.base.common.RabbitUiUtils
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.utils.dp2px
import kotlinx.android.synthetic.main.rabbit_page_memory_compose.view.*

/**
 * susionwang at 2019-12-03
 */
class RabbitMemoryComposePage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_memory_compose

    init {

        setTitle("内存概览")
        initChart(mRabbitMemComposePageMemChart)

        mRabbitMemComposePageTvMaxMem.text =
            RabbitUiUtils.formatFileSize(Runtime.getRuntime().maxMemory())

        mRabbitMemComposePageSRL.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    private fun loadData() {
        RabbitDbStorageManager.getAll(
            RabbitMemoryInfo::class.java,
            count = 200,
            orderDesc = false,
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
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                dp2px(200f)
            ).apply {
                val dp20 = dp2px(20f)
                setMargins(dp20, dp20, dp20, dp20)
            }
            setBackgroundColor(Color.parseColor("#90caf9"))
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