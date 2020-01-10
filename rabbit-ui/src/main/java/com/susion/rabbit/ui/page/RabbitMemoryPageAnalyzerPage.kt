package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.rabbit.base.entities.RabbitMemoryInfo
import com.susion.rabbit.base.greendao.RabbitMemoryInfoDao
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.utils.RabbitUiUtils
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.ui.entities.RabbitMemoryAnalyzerPageInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitMemPageInfoView
import kotlinx.android.synthetic.main.rabbbit_page_mem_analyzer_page_detail.view.*

/**
 * susionwang at 2020-01-10
 */
class RabbitMemoryPageAnalyzerPage(context: Context) : RabbitBasePage(context) {

    private val adapter = object : RabbitRvAdapter<RabbitMemoryAnalyzerPageInfo>(ArrayList()) {
        override fun getItemType(data: RabbitMemoryAnalyzerPageInfo) = 0

        override fun createItem(type: Int) = RabbitMemPageInfoView(context)
    }

    override fun getLayoutResId() = R.layout.rabbbit_page_mem_analyzer_page_detail

    init {

        setTitle("内存概览-页面")

        mMemDetailPageRv.adapter = adapter
        mMemDetailPageRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mMemDetailPageSRL.isRefreshing = true

        mMemDetailPageSRL.setOnRefreshListener {
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            loadData()
        }

        loadData()
    }

    private fun loadData() {
        RabbitDbStorageManager.distinct(
            RabbitMemoryInfo::class.java,
            RabbitMemoryInfoDao.Properties.PageName.columnName
        ) { pages ->

            pages.forEachIndexed { index, page ->

                loadMemAnalyzerInfoByPage(page) { memInfo ->

                    if (memInfo.pageName.isNotEmpty()) {
                        mMemDetailPageSRL.isRefreshing = false
                        adapter.data.add(memInfo)
                        adapter.notifyItemInserted(adapter.data.size - 1)
                    }
                }
            }
        }
    }

    private fun loadMemAnalyzerInfoByPage(
        pageName: String,
        loadedAnalyzerInfo: (analyzerInfo: RabbitMemoryAnalyzerPageInfo) -> Unit
    ) {
        RabbitDbStorageManager.getAll(
            RabbitMemoryInfo::class.java,
            Pair(RabbitMemoryInfoDao.Properties.PageName, pageName),
            loadResult = { mems ->

                val memInfo = RabbitMemoryAnalyzerPageInfo()

                memInfo.avgMem =RabbitUiUtils.formatFileSize( mems.map { it.totalSize }.average().toLong())

                memInfo.avgVmMem = RabbitUiUtils.formatFileSize(mems.map { it.vmSize }.average().toLong())

                memInfo.pageName = pageName

                memInfo.recordCount = mems.size

                loadedAnalyzerInfo(memInfo)

            })
    }

}