package com.susion.rabbit.ui.page

import android.content.Context
import android.os.Environment
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.susion.lifeclean.common.recyclerview.CommonRvAdapter
import com.susion.rabbit.base.common.FileUtils
import com.susion.rabbit.base.common.RabbitAsync
import com.susion.rabbit.base.entities.RabbitIoCallInfo
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.entities.RabbitBlockCallList
import com.susion.rabbit.ui.entities.RabbitUiSimpleCallInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.view.RabbitIoCallItemView
import kotlinx.android.synthetic.main.rabbit_page_io_call.view.*
import java.io.File

/**
 * susionwang at 2019-10-29
 */
class RabbitCodeScanPage(context: Context) : RabbitBasePage(context) {

    private val EXPORT_FILE_PATH =
        "${Environment.getExternalStorageDirectory()}/Rabbit/blockCall.txt"

    private val logsAdapter by lazy {

        object : CommonRvAdapter<RabbitIoCallInfo>(ArrayList()) {
            override fun createItem(type: Int) = RabbitIoCallItemView(context).apply {
                eventListener = object : RabbitIoCallItemView.EventListener {
                    override fun onClick(str: String) {
                        showToast(str, 3000)
                    }
                }
            }

            override fun getItemType(data: RabbitIoCallInfo) = 0
        }
    }

    override fun getLayoutResId() = R.layout.rabbit_page_io_call

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setTitle("代码扫描")
        actionBar.setRightOperate(R.drawable.rabbit_icon_export_io_call) {
            showToast("开始导出阻塞调用~")
            val ioCallSb = StringBuilder()
            logsAdapter.data.forEach {
                ioCallSb.append("${it.invokeStr} -> ${it.becalledStr}")
            }

            val exportList =
                logsAdapter.data.map { RabbitUiSimpleCallInfo(it.invokeStr, it.becalledStr) }
            val exportStr = Gson().toJson(RabbitBlockCallList(exportList))
            RabbitAsync.asyncRunWithResult({
                FileUtils.writeStrToFile(File(EXPORT_FILE_PATH), exportStr)
            }, {
                showToast("导出完毕~")
            })
        }

        mIoCallPageRv.adapter = logsAdapter
        mIoCallPageRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        loadData()
    }

    private fun loadData() {
        RabbitStorage.getAll(RabbitIoCallInfo::class.java) {
            if (it.isEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
                logsAdapter.data.clear()
                logsAdapter.data.addAll(it)
                logsAdapter.notifyDataSetChanged()
            }
        }
    }

}