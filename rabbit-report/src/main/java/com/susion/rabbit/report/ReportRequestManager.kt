package com.susion.rabbit.report

import android.util.Base64
import androidx.annotation.Keep
import com.google.gson.Gson
import com.susion.rabbit.RabbitLog
import com.susion.rabbit.entities.RabbitReportInfo
import okhttp3.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-12-05
 * 数据上报请求发射
 */
internal class ReportRequestManager {

    private val TAG = "rabbit-data-report"

    val EMITTER_CONNECT_TIMEOUT: Long = 15  //网络链接超时时间
    private val CONTENT_TYPE_JSON = "application/json; charset=utf-8"
    private val gson = Gson()

    //构建 ok http client
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(EMITTER_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(EMITTER_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .protocols(Collections.unmodifiableList(listOf(Protocol.HTTP_1_1)))
        .build()

    fun postTrackRequest(points: List<RabbitReportInfo>, requestListener: TrackRequestListener) {

        val trackUrl = RabbitReport.mConfig.reportPath

        RabbitLog.d(
            TAG,
            "target url : $trackUrl 准备发射 ${points.size} 个点位到服务器！  current execute thread is : ${Thread.currentThread().name}  "
        )

        //构建请求
        val trackRequest = Request.Builder()
            .url(trackUrl)
            .post(buildPostBody(points))
            .build()

        //请求服务器
        try {
            val resp = okHttpClient.newCall(trackRequest).execute()
            val code = resp.code()
            resp.body()?.close()
            if (isSuccessful(code)) {
                RabbitLog.d(TAG, "发射成功! 服务器返回码是： $code")
                requestListener.onRequestSucceed()
            } else {
                RabbitLog.d(TAG, "track request failed, response code is $code")
                requestListener.onRequestFailed()
            }
        } catch (e: Exception) {
            RabbitLog.d(TAG, "track request exception ${e.message}")
            requestListener.onRequestFailed()
        }
    }

    private fun buildPostBody(points: List<RabbitReportInfo>): RequestBody {

        val bodyArg = StringBuilder()

        points.forEachIndexed { index, pointInfo ->
            val strByteArr = gson.toJson(pointInfo).toByteArray()
            if (index != 0) {
                bodyArg.append("|")
                bodyArg.append(getBase64Encode(strByteArr))
            } else {
                bodyArg.append(getBase64Encode(strByteArr))
            }
        }

        val content = "$bodyArg"

        RabbitLog.d(TAG, content)

        return RequestBody.create(
            MediaType.parse(CONTENT_TYPE_JSON),
            gson.toJson(InnerRequestBody(content))
        )
    }

    private fun getBase64Encode(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT).trim()
    }

    private fun isSuccessful(code: Int): Boolean {
        return code in 200..299
    }

    interface TrackRequestListener {
        fun onRequestSucceed()
        fun onRequestFailed()
    }

    @Keep
    private class InnerRequestBody(val content: String, val type: String = "unknow")

}