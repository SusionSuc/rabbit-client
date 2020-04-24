package com.susion.rabbit.ui.page

import android.content.Context
import android.view.ViewGroup
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitBlockFrameInfo
import com.susion.rabbit.ui.view.RabbitUiBlockPreView
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.ui.monitor.R
import kotlinx.android.synthetic.main.rabbit_page_ui_block_list.view.*

/**
 * susionwang at 2019-10-29
 */
class RabbitUiBlockListPage(context: Context) : RabbitBasePage(context) {

    private val logsAdapter by lazy {
        SimpleRvAdapter<RabbitBlockFrameInfo>(context).apply {
            registerMapping(RabbitBlockFrameInfo::class.java, RabbitUiBlockPreView::class.java)
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
        RabbitStorage.getAll(RabbitBlockFrameInfo::class.java, orderDesc = true) {
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