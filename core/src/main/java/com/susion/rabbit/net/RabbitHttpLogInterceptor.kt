package com.susion.rabbit.net

import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * susionwang at 2019-09-24
 *
 * rabbit 网络日志功能
 */
class RabbitHttpLogInterceptor : Interceptor {

    private val TAG = "RabbitHttpLogInterceptor"
    private var startNs = System.nanoTime()

    override fun intercept(chain: Interceptor.Chain): Response? {

        val request = chain.request()

        val response = chain.proceed(request)

        if (!Rabbit.isOpen()) return response

        try {

            startNs = System.nanoTime()

            val logInfo = RabbitHttpResponseParser.parserResponse(request, response, startNs)

            if (logInfo.isvalid()) {
                RabbitDbStorageManager.save(logInfo)
            }

            return response

        } catch (e: Exception) {

            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")

            return response
        }
    }

}