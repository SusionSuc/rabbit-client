package com.susion.rabbit.net

import com.susion.rabbit.net.entities.RabbitHttpLogInfo
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okio.Buffer
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-09-24
 * parser response to specific info [RabbitHttpLogInfo]
 */
object RabbitHttpResponseParser {

    private val TAG = javaClass.simpleName

    fun parserResponse(request: Request, response: Response, startTime: Long): RabbitHttpLogInfo {
        return when {
            isSuccessResponse(response.code()) -> parseSuccessHttpLog(response, request, startTime)
            isErrorResponse(response.code()) -> parseErrorHttpLog(response, request, startTime)
            else -> RabbitHttpLogInfo()
        }
    }

    private fun parseErrorHttpLog(
        response: Response,
        request: Request,
        startTime: Long
    ): RabbitHttpLogInfo {
        val logInfo = RabbitHttpLogInfo()
        val reqHttpUrl = request.url()

        logInfo.apply {
            host = reqHttpUrl.host()
            path = reqHttpUrl.encodedPath()
            requestParams = getUrlRequestParams(request)
            requestBody = postRequestParams(request)
            tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            requestType = request.method()
            isSuccessRequest = false
            responseCode = response.code().toString()
        }
        return logInfo
    }

    private fun parseSuccessHttpLog(
        response: Response,
        request: Request,
        startTime: Long
    ): RabbitHttpLogInfo {
        val logInfo = RabbitHttpLogInfo()
        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize =
            if (contentLength != -1L) (contentLength).toString() + "-byte" else "unknown-length"

        val reqHttpUrl = request.url()

        if (supportParseType(responseBody.contentType())
            && HttpHeaders.hasBody(response)
            && !bodyHasUnknownEncoding(response.headers())
        ) {
            val source = responseBody.source()
            source.request(java.lang.Long.MAX_VALUE)    // Buffer the entire body.
            val buffer = source.buffer()

            var resStr = ""
            if (contentLength != 0L) {
                resStr = buffer.clone().readString(Charset.forName("UTF-8"))
            }

            logInfo.apply {
                host = reqHttpUrl.host()
                path = reqHttpUrl.encodedPath()
                requestParams = getUrlRequestParams(request)
                requestBody = postRequestParams(request)
                responseStr = resStr
                tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
                size = bodySize
                requestType = request.method()
                responseContentType = responseBody.contentType()?.type() ?: ""
            }
        }
        return logInfo
    }

    private fun postRequestParams(request: Request): String {
        if (request.method() != "POST" && request.body() != null && request.body()!!.contentLength() > 0) return ""
        val requestBuffer = Buffer()
        request.body()?.writeTo(requestBuffer)
        return String(requestBuffer.readByteArray())
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return (contentEncoding != null
                && !contentEncoding.equals("identity", ignoreCase = true)
                && !contentEncoding.equals("gzip", ignoreCase = true))
    }


    /**
     * 获取 GET 请求的请求参数
     * */
    private fun getUrlRequestParams(request: Request): HashMap<String, String> {
        val requestUrl = request.url().toString()
        val map = HashMap<String, String>()
        try {
            val charset = "utf-8"
            val decodedUrl = URLDecoder.decode(requestUrl, charset)
            if (decodedUrl.indexOf('?') != -1) {
                val contents = decodedUrl.substring(decodedUrl.indexOf('?') + 1)
                val keyValues =
                    contents.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in keyValues.indices) {
                    val key = keyValues[i].substring(0, keyValues[i].indexOf("="))
                    val value = keyValues[i].substring(keyValues[i].indexOf("=") + 1)
                    map[key] = value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    private fun supportParseType(contentType: MediaType?): Boolean {
        return SUPPORT_PARSE_TYPE.contains(contentType?.subtype())
    }

    private val SUPPORT_PARSE_TYPE = arrayOf("json", "GSON")

    private fun isSuccessResponse(code: Int) = code == 200

    private fun isErrorResponse(code: Int) = code in 400..599

}