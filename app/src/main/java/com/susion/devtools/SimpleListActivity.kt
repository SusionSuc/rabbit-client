package com.susion.devtools

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.susion.rabbit.base.RabbitBaseActivity
import com.susion.rabbit.base.adapter.RabbitRvAdapter
import com.susion.rabbit.base.view.RabbitSimpleKVItemView
import com.susion.rabbit.base.view.RabbitSimpleKvInfo
import kotlinx.android.synthetic.main.activity_simple_list_page.*

class SimpleListActivity : RabbitBaseActivity() {


    private val listAdapter=  object :RabbitRvAdapter<RabbitSimpleKvInfo>(ArrayList()){
        override fun createItem(type: Int) = RabbitSimpleKVItemView(this@SimpleListActivity)

        override fun getItemType(data: RabbitSimpleKvInfo) = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_list_page)

        setActionBar(mSimpleListPageActionBar)
        mSimpleListPageActionBar.setTitle("简单列表")
        mSimpleListPageActionBar.hideQuickFinishBtn()

        listAdapter.data.addAll(getData())
        mSimpleListPageRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mSimpleListPageRv.adapter = listAdapter
    }

    private fun getData(): List<RabbitSimpleKvInfo> {
        val list = ArrayList<RabbitSimpleKvInfo>()

        (0 until 30).forEach {
            list.add(RabbitSimpleKvInfo("item${it}", "$it"))
        }
        return list
    }

}
