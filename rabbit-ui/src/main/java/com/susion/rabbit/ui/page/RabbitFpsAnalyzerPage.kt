package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.rabbit.base.entities.RabbitFPSInfo
import com.susion.rabbit.base.greendao.RabbitFPSInfoDao
import com.susion.rabbit.base.greendao.RabbitPageSpeedInfoDao
import com.susion.rabbit.base.ui.adapter.RabbitAdapterItemView
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.entities.RabbitFpsAnalyzerInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitFpsAnalyzerView
import kotlinx.android.synthetic.main.rabbit_page_fps_analyzer.view.*

/**
 * create by susionwang at 2020-01-09
 * 应用FPS分析
 */
class RabbitFpsAnalyzerPage(context: Context) : RabbitBasePage(context) {

    private val adapter = object : RabbitRvAdapter<RabbitFpsAnalyzerInfo>(ArrayList()) {
        override fun getItemType(data: RabbitFpsAnalyzerInfo) = 0

        override fun createItem(type: Int) = RabbitFpsAnalyzerView(context)
    }

    override fun getLayoutResId() = R.layout.rabbit_page_fps_analyzer

    init {
        setTitle("FPS分析")

        mFpsAnalyzerPageTv.adapter = adapter
        mFpsAnalyzerPageTv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mFpsAnalyzerPageSRL.isRefreshing = true

        mFpsAnalyzerPageSRL.setOnRefreshListener {
            adapter.data.clear()
            loadData()
        }

        loadData()
    }

    private fun loadData() {
        RabbitDbStorageManager.distinct(
            RabbitFPSInfo::class.java,
            RabbitFPSInfoDao.Properties.PageName.columnName
        ) { pages ->

            pages.forEachIndexed { index, pageName ->

                loadFpsAnalyzerInfoByPage(pageName) { analyzerInfo ->
                    adapter.data.add(analyzerInfo)

                    if (index == pages.size - 1) {
                        adapter.notifyDataSetChanged()
                        mFpsAnalyzerPageSRL.isRefreshing = false
                    }
                }

            }
        }
    }

    private fun loadFpsAnalyzerInfoByPage(
        pageName: String,
        loadedAnalyzerInfo: (analyzerInfo: RabbitFpsAnalyzerInfo) -> Unit
    ) {
        RabbitDbStorageManager.getAll(
            RabbitFPSInfo::class.java,
            Pair(RabbitFPSInfoDao.Properties.PageName, pageName),
            loadResult = { fpses ->
                val analyzerInfo = RabbitFpsAnalyzerInfo(pageName)

                analyzerInfo.minFps = fpses.map { it.minFps }.average().toInt().toString()

                analyzerInfo.avgFps = fpses.map { it.avgFps }.average().toInt().toString()

                analyzerInfo.maxFps = fpses.map { it.maxFps }.average().toInt().toString()

                analyzerInfo.fpsCount = fpses.size

                loadedAnalyzerInfo(analyzerInfo)
            })
    }

}