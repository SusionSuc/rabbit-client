package com.susion.rabbit.ui.global

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.RabbitSettings
import com.susion.rabbit.base.TAG_UI
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitGlobalMonitorInfo
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.view.RabbitSwitchButton
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.RabbitUi
import com.susion.rabbit.ui.entities.RabbitGlobalModePreInfo
import com.susion.rabbit.ui.global.view.RabbitGlobalMonitorPreView
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_global_monitor_mode.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitGlobalMonitorModePage(context: Context) : RabbitBasePage(context) {

    private val adapter by lazy {
        object : RabbitRvAdapter<RabbitGlobalModePreInfo>(ArrayList()) {
            override fun createItem(type: Int) = RabbitGlobalMonitorPreView(context)
            override fun getItemType(data: RabbitGlobalModePreInfo) = 0
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_global_monitor_mode

    init {

        setTitle("全局性能测试")

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
        RabbitDbStorageManager.getAll(RabbitGlobalMonitorInfo::class.java) { monitorList ->
            RabbitAsync.asyncRunWithResult({
                ArrayList<RabbitGlobalModePreInfo>().apply {
                    monitorList.forEach { monitorInfo ->
                        this.add(0, RabbitGlobalMonitorDataParser.getGlobalMonitorPreInfo(monitorInfo))
                    }
                }
            }, {
                mRabbitPageGlobalMonitorModeSRL.isRefreshing = false
                adapter.data.clear()
                adapter.data.addAll(it)
                adapter.notifyDataSetChanged()
            })
        }
    }

}