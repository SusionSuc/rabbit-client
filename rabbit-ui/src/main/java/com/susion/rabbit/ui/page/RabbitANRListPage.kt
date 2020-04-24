package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitAnrInfo
import com.susion.rabbit.base.greendao.RabbitAnrInfoDao
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitAnrItemView
import kotlinx.android.synthetic.main.rabbit_page_anr_list.view.*

/**
 * susionwang at 2020-03-19
 */
class RabbitANRListPage(context: Context) : RabbitBasePage(context) {

    override fun getLayoutResId() = R.layout.rabbit_page_anr_list

    private val adapter by lazy {
        SimpleRvAdapter<RabbitAnrInfo>(context).apply {
            registerMapping(RabbitAnrInfo::class.java, RabbitAnrItemView::class.java)
        }
    }

    init {
        setTitle("ANR日志")
        mAnrListRv.adapter = adapter
        mAnrListRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        mAnrListSPL.isRefreshing = true

        loadData()

        mAnrListSPL.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        RabbitStorage.getAll(
            RabbitAnrInfo::class.java,
            condition = Pair(RabbitAnrInfoDao.Properties.Invalid, true),
            orderDesc = true
        ) {
            mAnrListSPL.isRefreshing = false
            if (it.isNotEmpty()) {
                hideEmptyView()
                adapter.data.clear()
                adapter.data.addAll(it)
                adapter.notifyDataSetChanged()
            } else {
                showEmptyView()
            }
        }
    }

}
