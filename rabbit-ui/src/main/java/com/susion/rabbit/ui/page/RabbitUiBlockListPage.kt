package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.ui.view.RabbitUiBlockPreView
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitUiBlockListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        object : com.susion.rabbit.base.ui.adapter.RabbitRvAdapter<RabbitBlockFrameInfo>(ArrayList()) {
            override fun createItem(type: Int) =
                RabbitUiBlockPreView(context)
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
        RabbitDbStorageManager.getAll(RabbitBlockFrameInfo::class.java, orderDesc = true) {
            mUiBlockPageSRL.isRefreshing = false

            if (it.isEmpty()){
                showEmptyView()
            }else{
                hideEmptyView()
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }

        }
    }

}