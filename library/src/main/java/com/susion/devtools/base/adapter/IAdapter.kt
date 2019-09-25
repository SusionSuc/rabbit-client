package com.susion.devtools.base.adapter

import android.support.annotation.Keep

interface IAdapter<T> {

    /**
     * @param t list中的一条数据
     * @return pageType
     */
    fun getItemType(t: T): Int

    /**
     * 当缓存中无法得到所需item时才会调用
     *
     * @param type 通过[.getItemType]得到的type
     * @return 任意类型的 AdapterItemView
     */
    @Keep
    fun createItem(type: Int): AdapterItemView<Any>

}
