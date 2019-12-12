package com.susion.rabbit.base

import com.susion.rabbit.entities.UnZipApkFileInfo

/**
 * susionwang at 2019-11-29
 */

interface AnalyzerTask {

    // analyzer result json str
    fun analyze(unZipContext: UnZipApkFileInfo): String

    fun getResultName(): String

}