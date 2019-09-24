package com.susion.devtools.net

import okhttp3.Interceptor
import okhttp3.Response

/**
 * susionwang at 2019-09-24
 */
class DevToolsHttpLogInterceptor : Interceptor {

    var startNs = System.nanoTime()

    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            startNs = System.nanoTime()
            val response = chain.proceed(chain.request())
            val logInfo = HttpResponseParser.parserResponse(response, startNs)
            HttpLogStorageManager.saveLogInfoToLocal(logInfo)
            return response
        } catch (e: Exception) {
            throw e
        }
    }

}