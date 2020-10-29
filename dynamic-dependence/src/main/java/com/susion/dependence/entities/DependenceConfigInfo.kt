package com.susion.dependence.entities

import com.google.gson.annotations.SerializedName

/**
 * wangpengcheng.wpc create at 2020/10/29
 * */
class DependenceConfigInfo(
    @SerializedName("module_list")
    val moduleList: List<ModuleDependence>? = null
)