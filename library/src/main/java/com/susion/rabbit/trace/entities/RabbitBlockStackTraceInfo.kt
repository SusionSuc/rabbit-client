package com.susion.rabbit.trace.entities

/**
 * susionwang at 2019-11-01
 */

class RabbitBlockStackTraceInfo (val stackTrace:String = ""){

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
}