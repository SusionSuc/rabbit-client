package com.susion.rabbit.ui.utils

/**
 * susionwang at 2019-10-18
 */

object RabbitTimeUtils{

    //1s = 1000 000 000 ns
    fun convertNanoToSeconeds(nano:Long):Float{
        return  (nano * 1.0f) / 1000000000
    }

    //1s = 1000 ms
    fun convertMillisToSeconds(millis:Long):Float{
        return (millis * 1.0f) / 1000
    }

    //1ms = 1000 000 ns
    fun convertNsToMs(nano:Long):Float{
        return nano * 1.0f / 1000000
    }

}