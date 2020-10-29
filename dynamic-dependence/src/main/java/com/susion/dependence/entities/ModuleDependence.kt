package com.susion.dependence.entities

import com.google.gson.annotations.SerializedName
import com.susion.dependence.entities.DependenceInfo

/**
 * wangpengcheng.wpc create at 2020/10/28
 * */
class ModuleDependence(
    val name: String? = null,
    @SerializedName("dependence_list")
    val dependenceList: List<DependenceInfo>? = null
)