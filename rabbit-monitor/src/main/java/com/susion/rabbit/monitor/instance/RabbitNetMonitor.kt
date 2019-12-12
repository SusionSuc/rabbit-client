package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.monitor.core.RabbitMonitorProtocol
import com.susion.rabbit.monitor.utils.RabbitHttpResponseParser
import com.susion.rabbit.storage.RabbitDbStorageManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * susionwang at 2019-12-12
 * 1. 记录网络请求日志
 * 2. 记录某些指定的请求的时间
 */
class RabbitNetMonitor : RabbitMonitorProtocol, Interceptor {

    private var startNs = System.nanoTime()
    private val TAG = javaClass.simpleName

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)

        if (!isOpen()) return response

        monitorHttpLog(request, response)

        monitorHttpCostTime(request, response)

        return response
    }

    private fun monitorHttpLog(request: Request, response: Response) {
        try {

            startNs = System.nanoTime()

            val logInfo = RabbitHttpResponseParser.parserResponse(request, response, startNs)

            if (logInfo.isvalid()) {
                RabbitDbStorageManager.save(logInfo)
            }


        } catch (e: Exception) {
            RabbitLog.d(TAG, "RabbitHttpLogInterceptor error : ${e.printStackTrace()}")

        }
    }

    private fun monitorHttpCostTime(request: Request, response: Response) {
        val startTime = System.currentTimeMillis()
        val requestUrl = request.url().url().toString()

        if (!RabbitMonitor.monitorRequest(requestUrl)) return

        try {

            val costTime = System.currentTimeMillis() - startTime

            RabbitMonitor.markRequestFinish(requestUrl, costTime)

        } catch (e: Exception) {
            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        }
    }

    override fun open(context: Context) {

    }

    override fun close() {
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.NET

    override fun isOpen(): Boolean {
        return true
    }

}