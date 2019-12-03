package com.susion.rabbit.performance.core

import com.susion.rabbit.Rabbit
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.performance.RabbitMonitorManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * susionwang at 2019-11-21
 *
 * rabbit 页面测试，
 * 1. 监听请求是否完成
 * 2. 监听请求耗时
 */
class RabbitAppSpeedInterceptor : Interceptor {

    private val TAG = javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {

        val startTime = System.currentTimeMillis()
        val request = chain.request()
        val requestUrl = request.url().url().toString()
        val response = chain.proceed(request)

        if (!Rabbit.isOpen() || !RabbitMonitorManager.isOpen(RabbitMonitor.APP_SPEED.enName)) return response

        if (!RabbitMonitorManager.monitorRequest(requestUrl)) return response

        try {

            val costTime = System.currentTimeMillis() - startTime

            RabbitMonitorManager.markRequestFinish(requestUrl, costTime)

        } catch (e: Exception) {
            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        } finally {
            return response
        }

    }

}