package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.base.TAG_MONITOR_UI
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.base.greendao.RabbitFPSInfoDao
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.entities.RabbitFpsAnalyzerInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitFpsAnalyzerPreView
import kotlinx.android.synthetic.main.rabbit_page_fps_analyzer.view.*

/**
 * create by susionwang at 2020-01-09
 * 应用FPS分析
 */
class RabbitFpsAnalyzerListPage(context: Context) : RabbitBasePage(context) {

    private val adapter = object : RabbitRvAdapter<RabbitFpsAnalyzerInfo>(ArrayList()) {
        override fun getItemType(data: RabbitFpsAnalyzerInfo) = 0

        override fun createItem(type: Int) = RabbitFpsAnalyzerPreView(context)
    }

    override fun getLayoutResId() = R.layout.rabbit_page_fps_analyzer

    init {
        setTitle("FPS分析")

        mFpsAnalyzerPageTv.adapter = adapter
        mFpsAnalyzerPageTv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mFpsAnalyzerPageSRL.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    private fun loadData() {

        RabbitDbStorageManager.distinct(
            RabbitFPSInfo::class.java,
            RabbitFPSInfoDao.Properties.PageName.columnName
        ) { pages ->

            RabbitLog.d(TAG_MONITOR_UI, "RabbitFPSInfo pages size :${pages.size}")

            adapter.data.clear()

            if (pages.isEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
                pages.forEachIndexed { index, pageName ->
                    adapter.data.add(loadFpsAnalyzerInfoByPage(pageName))
                }
                adapter.notifyDataSetChanged()
            }

            mFpsAnalyzerPageSRL.isRefreshing = false
        }
    }

    private fun loadFpsAnalyzerInfoByPage(pageName: String): RabbitFpsAnalyzerInfo {

        val fpses = RabbitDbStorageManager.getAllSync(
            RabbitFPSInfo::class.java,
            Pair(RabbitFPSInfoDao.Properties.PageName, pageName)
        )

        val analyzerInfo = RabbitFpsAnalyzerInfo(pageName)

        analyzerInfo.minFps = fpses.map { it.minFps }.min()?.toInt().toString()

        analyzerInfo.avgFps = fpses.map { it.avgFps }.average().toInt().toString()

        analyzerInfo.maxFps = fpses.map { it.maxFps }.max()?.toInt().toString()

        analyzerInfo.fpsCount = fpses.size

        return analyzerInfo

    }

}