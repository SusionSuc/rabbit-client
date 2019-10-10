package com.susion.devtools.net

import android.util.Log
import com.susion.devtools.DevTools
import okhttp3.Interceptor
import okhttp3.Response

/**
 * susionwang at 2019-09-24
 */
class DevToolsHttpLogInterceptor : Interceptor {

    private var startNs = System.nanoTime()

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        try {
            startNs = System.nanoTime()

            if (!DevTools.devToolsIsOpen()) return response

            val logInfo = HttpResponseParser.parserResponse(chain.request(), response, startNs)

            HttpLogStorageManager.saveLogInfoToLocal(logInfo)

            return response

        } catch (e: Exception) {

            Log.d("DevTools", "DevToolsHttpLogInterceptor error")

        }

        return response

    }

}