package com.susion.devtools.base.adapter

interface AdapterItemView<T> {
    /**
     * 根据数据来设置item的内部views
     *
     * @param t    数据list内部的model
     * @param position 当前adapter调用item的位置
     */
    fun bindData(t: T, position: Int)
}
