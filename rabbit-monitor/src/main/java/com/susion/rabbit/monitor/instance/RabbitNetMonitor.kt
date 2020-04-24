package com.susion.rabbit.monitor.instance

import android.content.Context
import com.susion.rabbit.base.RabbitLog
import com.susion.rabbit.monitor.RabbitMonitor
import com.susion.rabbit.base.RabbitMonitorProtocol
import com.susion.rabbit.base.TAG_MONITOR
import com.susion.rabbit.monitor.utils.RabbitHttpResponseParser
import com.susion.rabbit.storage.RabbitStorage
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-12-12
 * 1. 记录网络请求日志
 * 2. 记录某些指定的请求的时间
 */
internal class RabbitNetMonitor(override var isOpen: Boolean = false) : RabbitMonitorProtocol, Interceptor {

    private var startNs = System.nanoTime()

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()

        startNs = System.nanoTime()

        try {
            val response = chain.proceed(request)

            if (!isOpen) return response

            monitorHttpLog(request, response)

            monitorHttpCostTime(request, response)

            return response

        }catch (e:Exception){

            RabbitStorage.save(RabbitHttpResponseParser.createExceptionLog(request, e))

            throw e
        }
    }

    private fun monitorHttpLog(request: Request, response: Response) {
        try {
            val logInfo = RabbitHttpResponseParser.parserResponse(request, response, startNs)

            if (logInfo.isvalid()) {
                RabbitStorage.save(logInfo)
            }

        } catch (e: Exception) {
            RabbitLog.d(TAG_MONITOR, "RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        }
    }

    private fun monitorHttpCostTime(request: Request, response: Response) {

        val requestUrl = request.url().url().toString()

        if (!RabbitMonitor.monitorRequest(requestUrl)) return

        try {

            val costTime = System.nanoTime() - startNs

            RabbitMonitor.markRequestFinish(requestUrl, TimeUnit.MILLISECONDS.convert(costTime, TimeUnit.NANOSECONDS))

        } catch (e: Exception) {
            RabbitLog.d("RabbitHttpLogInterceptor error : ${e.printStackTrace()}")
        }
    }

    override fun open(context: Context) {
        isOpen = true
    }

    override fun close() {
        isOpen = false
    }

    override fun getMonitorInfo() = RabbitMonitorProtocol.NET


}