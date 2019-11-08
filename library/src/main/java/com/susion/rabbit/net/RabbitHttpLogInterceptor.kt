package com.susion.rabbit.net

import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.db.RabbitDbStorageManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * susionwang at 2019-09-24
 */

class RabbitHttpLogInterceptor : Interceptor {

    private var startNs = System.nanoTime()

    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())

        try {
            startNs = System.nanoTime()

            if (!Rabbit.isOpen()) return response

            val logInfo = RabbitHttpResponseParser.parserResponse(chain.request(), response, startNs)

            if (logInfo.isvalid()){
                RabbitDbStorageManager.save(logInfo)
            }

            return response

        } catch (e: Exception) {
            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        }

        return response

    }

}