package com.susion.rabbit.base

import com.susion.rabbit.Config

/**
 * susionwang at 2019-11-29
 */

interface AnalyzerTask {

    fun analyze(config: Config): String

    fun getResultName(): String

}