package com.susion.rabbit.ui.page

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.susion.rabbit.base.entities.RabbitSlowMethodInfo
import com.susion.rabbit.storage.RabbitDbStorageManager
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.base.ui.adapter.RabbitRvAdapter
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.entities.RabbitSlowMethodUiInfo
import com.susion.rabbit.ui.view.RabbitSlowMethodInfoView

/**
 * susionwang at 2020-01-02
 */
class RabbitSlowMethodDetailPage(context: Context) : RabbitBasePage(context) {

    private val mAdapter = object : RabbitRvAdapter<RabbitSlowMethodUiInfo>(ArrayList()) {
        override fun createItem(type: Int) = RabbitSlowMethodInfoView(context)
        override fun getItemType(data: RabbitSlowMethodUiInfo) = 0
    }

    private val methodRv = RecyclerView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            topMargin = ACTION_BAR_HEIGHT
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = mAdapter
    }

    init {
        background = getDrawable(context, R.color.rabbit_white)
        addView(methodRv)
    }

    override fun setEntryParams(params: Any) {

        val pkgName = params as String

        setTitle(pkgName)

        val uiMethodInfo = LinkedHashMap<String, RabbitSlowMethodUiInfo>()

        RabbitDbStorageManager.getAll(
            RabbitSlowMethodInfo::class.java,
            loadResult = {

                it.sortedByDescending { it.costTimeMs }.filter { it.pkgName.contains(pkgName) }
                    .forEach {
                        val keyIden = "${it.className}_${it.methodName}"
                        var info = uiMethodInfo[keyIden]
                        if (info == null) {
                            info = RabbitSlowMethodUiInfo(
                                it.className,
                                it.methodName,
                                0,
                                0,
                                it.callStack
                            )
                            uiMethodInfo[keyIden] = info
                        }

                        info.count++
                        info.totalTime += it.costTimeMs
                    }

                mAdapter.data.addAll(uiMethodInfo.values)
                mAdapter.notifyDataSetChanged()
            })

    }

    override fun getLayoutResId() = INVALID_RES_ID

}