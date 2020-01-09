package com.susion.rabbit.ui.entities

/**
 * susionwang at 2020-01-02
 */
class RabbitSlowMethodUiInfo (val className:String, val name:String, var count:Int, var totalTime:Long, var callStack:String = ""){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RabbitSlowMethodUiInfo

        if (className != other.className) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}