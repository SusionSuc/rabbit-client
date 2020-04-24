package com.susion.rabbit.ui.slowmethod

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.susion.lifeclean.common.recyclerview.SimpleRvAdapter
import com.susion.rabbit.base.entities.RabbitSlowMethodInfo
import com.susion.rabbit.base.ui.getDrawable
import com.susion.rabbit.base.ui.page.RabbitBasePage
import com.susion.rabbit.storage.RabbitStorage
import com.susion.rabbit.ui.entities.RabbitSlowMethodUiInfo
import com.susion.rabbit.ui.monitor.R
import com.susion.rabbit.ui.slowmethod.view.RabbitSlowMethodInfoView

/**
 * susionwang at 2020-01-02
 */
class RabbitSlowMethodPkgListPage(context: Context) : RabbitBasePage(context) {

    private val mAdapter =SimpleRvAdapter<RabbitSlowMethodUiInfo>(context).apply {
        registerMapping(RabbitSlowMethodUiInfo::class.java,RabbitSlowMethodInfoView::class.java )
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

        RabbitStorage.getAll(
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