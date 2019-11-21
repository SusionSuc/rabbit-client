package com.susion.rabbit.tracer.monitor

import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.tracer.RabbitTracer
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * susionwang at 2019-11-21
 *
 * rabbit 页面测试，监听请求是否完成
 */
class RabbitAppSpeedInterceptor : Interceptor {

    private val TAG = javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {

        val startTime = System.currentTimeMillis()

        val request = chain.request()

        val response = chain.proceed(request)

        if (!Rabbit.isOpen() || !RabbitTracer.pageSpeedMonitorIsOpen()) return response

        try {

            RabbitLog.d(TAG, " ${chain.request().url()} --> request cost time ${System.currentTimeMillis() - startTime}")

            RabbitTracer.markRequestFinish(request.url().url().toString())

        } catch (e: Exception) {
            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        } finally {
            return response
        }

    }

}