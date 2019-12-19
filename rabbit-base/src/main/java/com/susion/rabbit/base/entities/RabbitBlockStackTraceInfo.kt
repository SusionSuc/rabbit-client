package com.susion.rabbit.base.entities

/**
 * susionwang at 2019-11-01
 */

class RabbitBlockStackTraceInfo (val stackTrace:String = "", var collectCount:Int = 1){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RabbitBlockStackTraceInfo

        if (stackTrace != other.stackTrace) return false

        return true
    }

    override fun hashCode(): Int {
        return stackTrace.hashCode()
    }


    fun getMapKey() = hashCode().toString()
}