package com.susion.rabbit.demo.page

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.demo.R
import com.susion.rabbit.demo.RabbitBaseActivity
import com.susion.rabbit.base.ui.view.RabbitSimpleKVItemView
import com.susion.rabbit.base.ui.view.RabbitSimpleKvInfo
import com.susion.rabbit.demo.test.Test
import kotlinx.android.synthetic.main.activity_simple_list_page.*

class SimpleListActivity : RabbitBaseActivity() {

    private val listAdapter = SimpleRvAdapter<RabbitSimpleKvInfo>(this).apply {
        registerMapping(RabbitSimpleKvInfo::class.java, RabbitSimpleKVItemView::class.java)
    }

    private val a = 1

    private val handler = object : Handler() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_list_page)

        setActionBar(mSimpleListPageActionBar)
        mSimpleListPageActionBar.setTitle("简单列表")

        listAdapter.data.addAll(getData())

        mSimpleListPageRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mSimpleListPageRv.adapter = listAdapter

        getSharedPreferences("test", Context.MODE_PRIVATE).edit().putBoolean("111", true).commit()


        val handler = object :Handler(){

        }

        val test = object : Test(){

        }
    }

    private fun getData(): List<RabbitSimpleKvInfo> {
        val list = ArrayList<RabbitSimpleKvInfo>()

        (0 until 30).forEach {
            list.add(RabbitSimpleKvInfo("item${it}", "$it"))
        }

        return list
    }

}
