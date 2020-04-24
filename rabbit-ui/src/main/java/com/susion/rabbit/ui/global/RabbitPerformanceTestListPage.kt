package com.susion.rabbit.ui.global

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitAppPerformanceInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.global.entities.RabbitAppPerformancePitInfo
import com.susion.rabbit.ui.global.view.RabbitAppPerformancePreView
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.page.RabbitCurrentConfigPage
import kotlinx.android.synthetic.main.rabbit_page_performance_test_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitPerformanceTestListPage(context: Context) : RabbitBasePage(context) {

    private val adapter = SimpleRvAdapter<RabbitAppPerformancePitInfo>(context).apply {
        registerMapping(
            RabbitAppPerformancePitInfo::class.java,
            RabbitAppPerformancePreView::class.java
        )
    }

    override fun getLayoutResId() = R.layout.rabbit_page_performance_test_list

    init {

        setTitle("全局性能测试")

        actionBar.setRightOperate(R.drawable.rabbit_icon_current_config) {
            RabbitUi.openPage(RabbitCurrentConfigPage::class.java)
        }

        mRabbitPageGlobalMonitorModeRv.adapter = adapter
        mRabbitPageGlobalMonitorModeRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mRabbitPageGlobalMonitorModeSwitchBtn.refreshUi(
            "性能测试模式",
            RabbitSettings.autoOpen(context, RabbitMonitorProtocol.GLOBAL_MONITOR.name)
        )

        mRabbitPageGlobalMonitorModeSwitchBtn.checkedStatusChangeListener =
            object : RabbitSwitchButton.CheckedStatusChangeListener {
                override fun checkedStatusChange(isChecked: Boolean) {
                    if (isChecked) {
                        showToast("重启应用后生效!")
                    }
                    RabbitUi.eventListener?.changeGlobalMonitorStatus(isChecked)
                }
            }

        mRabbitPageGlobalMonitorModeSRL.isRefreshing = true

        loadData()

        mRabbitPageGlobalMonitorModeSRL.setOnRefreshListener {
            loadData()
        }

    }

    private fun loadData() {
        RabbitStorage.getAll(RabbitAppPerformanceInfo::class.java) { monitorList ->
            RabbitAsync.asyncRunWithResult({
                ArrayList<RabbitAppPerformancePitInfo>().apply {
                    monitorList.forEach { monitorInfo ->
                        this.add(
                            0,
                            RabbitPerformanceTestDataAnalyzer.getGlobalMonitorSimpleInfo(monitorInfo)
                        )
                    }
                }
            }, {
                mRabbitPageGlobalMonitorModeSRL.isRefreshing = false
                adapter.data.clear()
                adapter.data.addAll(it)
                adapter.notifyDataSetChanged()
                if (adapter.data.isEmpty()) {
                    showEmptyView()
                } else {
                    hideEmptyView()
                }
            })
        }
    }

}