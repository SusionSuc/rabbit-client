package com.susion.rabbit.utils

import java.text.DecimalFormat

/**
 * susionwang at 2019-11-29
 */

object Utils{
    fun formatImageSize(size: Long): String {
        if (size <= 0) return ""
        val formater = DecimalFormat("####")
        return if (size < 1024) {
            size.toString() + "B"
        } else if (size < 1024 * 1024) {
            val kbsize = size / 1024f
            formater.format(kbsize) + "KB"
        } else if (size < 1024 * 1024 * 1024) {
            val mbsize = size / 1024f / 1024f
            formater.format(mbsize) + "MB"
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            val gbsize = size / 1024f / 1024f / 1024f
            formater.format(gbsize) + "GB"
        } else ""
    }

}