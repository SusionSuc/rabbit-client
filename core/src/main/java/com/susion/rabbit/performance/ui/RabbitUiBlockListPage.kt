package com.susion.rabbit.performance.ui

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.db.RabbitDbStorageManager
import com.susion.rabbit.performance.entities.RabbitBlockFrameInfo
import com.susion.rabbit.ui.page.RabbitBasePage
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitUiBlockListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<RabbitBlockFrameInfo>(ArrayList()) {
            override fun createItem(type: Int) =
                RabbitUiBlockItemView(context)
            override fun getItemType(data: RabbitBlockFrameInfo) = 0
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_ui_block_list

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("卡顿日志")

        mUiBlockPageRv.adapter = logsAdapter
        mUiBlockPageRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )
        loadData()

        mUiBlockPageSRL.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        RabbitDbStorageManager.getAll(RabbitBlockFrameInfo::class.java) {
            mUiBlockPageSRL.isRefreshing = false
            logsAdapter.data.clear()
            logsAdapter.data.addAll(it)
            logsAdapter.notifyDataSetChanged()
        }
    }

}