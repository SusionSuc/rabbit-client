package com.susion.rabbit.base.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * create by susion
 * 简单的做数据 到 UI 的映射
 */
abstract class RabbitRvAdapter<T>(val data: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommonViewHolder(
            createItem(
                viewType
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CommonViewHolder<T>).item.bindData(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return getItemType(data[position])
    }

    /**
     * @param data list中的一条数据
     * @return pageType
     */
    abstract fun getItemType(data: T): Int

    /**
     * 当缓存中无法得到所需item时才会调用
     *
     * @param type 通过[.getItemType]得到的type
     * @return 任意类型的 RabbitAdapterItemView
     */
    abstract fun createItem(type: Int): RabbitAdapterItemView<*>

    private class CommonViewHolder<T> internal constructor(var item: RabbitAdapterItemView<T>) :
        RecyclerView.ViewHolder(if (item is View) item else throw RuntimeException("RabbitRvAdapter item view must is view"))

}
