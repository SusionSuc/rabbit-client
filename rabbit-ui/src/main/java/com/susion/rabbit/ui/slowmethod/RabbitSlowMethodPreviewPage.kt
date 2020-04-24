package com.susion.rabbit.ui.slowmethod

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.susion.rabbit.base.entities.RabbitSlowMethodInfo
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.tracer.RabbitPluginConfig
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.ui.entities.RabbitSlowMethodGroupInfo
import com.susion.rabbit.ui.slowmethod.view.RabbitSlowMethodGroupItemView
import kotlinx.android.synthetic.main.rabbit_page_slow_method_list.view.*
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.formatter.PercentFormatter
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.ui.monitor.R

/**
 * susionwang at 2020-01-02
 */
class RabbitSlowMethodPreviewPage(context: Context) : RabbitBasePage(context) {

    val colorList = listOf(
        Color.parseColor("#42a5f5"),
        Color.parseColor("#f06292"),
        Color.parseColor("#ce93d8"),
        Color.parseColor("#00e676"),
        Color.parseColor("#18ffff"),
        Color.parseColor("#ff80ab"),
        Color.parseColor("#66bb6a")
    )

    private val adapter = SimpleRvAdapter<RabbitSlowMethodGroupInfo>(context).apply {
        registerMapping(RabbitSlowMethodGroupInfo::class.java, RabbitSlowMethodGroupItemView::class.java)
    }

    override fun getLayoutResId() = R.layout.rabbit_page_slow_method_list

    init {

        background = getDrawable(context, R.color.rabbit_white)
        setTitle("慢函数")
        mRabbitSlowMethodChart.setDrawCenterText(false)
        mRabbitSlowMethodChart.setDrawEntryLabels(false)
        mRabbitSlowMethodChart.holeRadius = 0f
        mRabbitSlowMethodChart.transparentCircleRadius = 0f

        mRabbitSlowMethodListPageSRL.setOnRefreshListener {
            loadData()
        }

        mRabbitSlowMethodRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRabbitSlowMethodRv.adapter = adapter

        loadData()

    }

    private fun loadData() {
        val methodGroupMap = HashMap<String, Int>()
        RabbitPluginConfig.methodMonitorPkgs.forEach {
            methodGroupMap[it] = 0
        }

        val methodCountMap = HashMap<String, Int>()

        RabbitStorage.getAll(RabbitSlowMethodInfo::class.java, loadResult = {

            if (it.isEmpty()) {
                showEmptyView()
                mRabbitSlowMethodListPageSRL.visibility = View.GONE
            } else {
                hideEmptyView()
                mRabbitSlowMethodListPageSRL.visibility = View.VISIBLE
            }

            mRabbitSlowMethodListPageSRL.isRefreshing = false

            it.forEach { trackMethod ->
                val methodName =
                    "${trackMethod.pkgName}${trackMethod.className}${trackMethod.methodName}"

                RabbitPluginConfig.methodMonitorPkgs.forEach { configMethodPkg ->
                    if (trackMethod.pkgName.contains(configMethodPkg)) {
                        methodGroupMap[configMethodPkg] = (methodGroupMap[configMethodPkg] ?: 0) + 1
                    }
                }

                methodCountMap[methodName] = (methodCountMap[methodName] ?: 0) + 1
            }

            val pieEntryList = ArrayList<PieEntry>()

            for (pkgInfo in methodGroupMap.entries) {
                pieEntryList.add(PieEntry(pkgInfo.value.toFloat(), pkgInfo.key))
            }

            mRabbitSlowMethodChart.data = PieData(PieDataSet(pieEntryList, "").apply {
                colors = colorList
                valueFormatter = PercentFormatter()
                setDrawValues(true)
            })
            mRabbitSlowMethodChart.invalidate()

            val groupInfos = methodGroupMap.map {
                RabbitSlowMethodGroupInfo(
                    it.key,
                    it.value
                )
            }
            for (methodCountInfo in methodCountMap.entries) {
                groupInfos.forEach { groupInfo ->
                    if (methodCountInfo.key.contains(groupInfo.pkgName)) {
                        groupInfo.methodCount = methodCountInfo.value
                    }
                }
            }

            adapter.data.clear()
            adapter.data.addAll(groupInfos.filter { it.methodCount > 0 && it.slowMethodRecord > 0 })
            adapter.notifyDataSetChanged()

        })
    }

}