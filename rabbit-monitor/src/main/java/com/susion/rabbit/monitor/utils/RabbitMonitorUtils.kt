package com.susion.rabbit.monitor.utils

/**
 * susionwang at 2020-01-06
 */
object RabbitMonitorUtils {

    fun traceToString(skipStackCount: Int, stackArray: Array<StackTraceElement>, maxLineCount:Int = 20): String {
        if (stackArray.isEmpty()) {
            return "[]"
        }

        val b = StringBuilder()
        for (i in 0 until stackArray.size - skipStackCount) {
            if (i <= skipStackCount) {
               continue
            }
            b.append(stackArray[i])
            b.append("\n")
            if (i > maxLineCount){
                break
            }
        }

        return b.toString()
    }

}