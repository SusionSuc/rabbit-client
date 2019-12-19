package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.R
import com.susion.rabbit.ui.base.RabbitBasePage
import com.susion.rabbit.ui.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.ui.view.RabbitUiBlockItemView
import com.susion.rabbit.storage.RabbitDbStorageManager
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitUiBlockListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : RabbitRvAdapter<com.susion.rabbit.base.entities.RabbitBlockFrameInfo>(ArrayList()) {
            override fun createItem(type: Int) =
                RabbitUiBlockItemView(context)
            override fun getItemType(data: com.susion.rabbit.base.entities.RabbitBlockFrameInfo) = 0
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
        RabbitDbStorageManager.getAll(com.susion.rabbit.base.entities.RabbitBlockFrameInfo::class.java) {
            mUiBlockPageSRL.isRefreshing = false
            logsAdapter.data.clear()
            logsAdapter.data.addAll(it)
            logsAdapter.notifyDataSetChanged()
        }
    }

}