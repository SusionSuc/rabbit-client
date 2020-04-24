package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.base.greendao.RabbitMemoryInfoDao
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.entities.RabbitMemoryAnalyzerPageInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitMemPageInfoView
import kotlinx.android.synthetic.main.rabbbit_page_mem_analyzer_page_detail.view.*

/**
 * susionwang at 2020-01-10
 */
class RabbitMemoryPageAnalyzerPage(context: Context) : RabbitBasePage(context) {

    private val adapter = SimpleRvAdapter<RabbitMemoryAnalyzerPageInfo>(context).apply {
        registerMapping(RabbitMemoryAnalyzerPageInfo::class.java, RabbitMemPageInfoView::class.java)
    }

    override fun getLayoutResId() = R.layout.rabbbit_page_mem_analyzer_page_detail

    init {

        setTitle("内存概览-页面")

        mMemDetailPageRv.adapter = adapter
        mMemDetailPageRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mMemDetailPageSRL.setOnRefreshListener {
            loadData()
        }

        loadData()
    }

    private fun loadData() {
        RabbitStorage.distinct(
            RabbitMemoryInfo::class.java,
            RabbitMemoryInfoDao.Properties.PageName.columnName
        ) { pages ->

            adapter.data.clear()

            if (pages.isEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
            }

            pages.forEachIndexed { index, page ->
                adapter.data.add(loadMemAnalyzerInfoByPage(page))
            }

            mMemDetailPageSRL.isRefreshing = false
            adapter.notifyDataSetChanged()

        }
    }

    private fun loadMemAnalyzerInfoByPage(pageName: String): RabbitMemoryAnalyzerPageInfo {
        val mems = RabbitStorage.getAllSync(
            RabbitMemoryInfo::class.java,
            Pair(RabbitMemoryInfoDao.Properties.PageName, pageName)
        )

        val memInfo = RabbitMemoryAnalyzerPageInfo()

        memInfo.avgMem = RabbitUiUtils.formatFileSize(mems.map { it.totalSize }.average().toLong())

        memInfo.avgVmMem = RabbitUiUtils.formatFileSize(mems.map { it.vmSize }.average().toLong())

        memInfo.pageName = pageName

        memInfo.recordCount = mems.size

        return memInfo
    }

}