package com.susion.devtools.net

import android.util.Log
import com.susion.devtools.net.entities.HttpLogInfo
import okhttp3.Headers
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * susionwang at 2019-09-24
 */
object HttpResponseParser {

    private val TAG = javaClass.simpleName

    fun parserResponse(response: Response, startTime: Long): HttpLogInfo {

        val logInfo = HttpLogInfo()

        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize =
            if (contentLength != -1L) (contentLength).toString() + "-byte" else "unknown-length"

        val reqHttpUrl = response.request().url()

        if (HttpHeaders.hasBody(response) && !bodyHasUnknownEncoding(response.headers())) {
            val source = responseBody.source()
            source.request(java.lang.Long.MAX_VALUE)    // Buffer the entire body.
            val buffer = source.buffer()

            var resStr = ""
            if (contentLength != 0L) {
                resStr = buffer.clone().readString(Charset.forName("UTF-8"))
                Log.d(TAG, "response : $resStr")
            }

            logInfo.apply {
                host = reqHttpUrl.host()
                path = reqHttpUrl.encodedPath()
                getRequestParams = getUrlRequestParams(reqHttpUrl.url().toString())
                responseStr = resStr
                tookTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
                size = bodySize
            }

        }

        return logInfo
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return (contentEncoding != null
                && !contentEncoding.equals("identity", ignoreCase = true)
                && !contentEncoding.equals("gzip", ignoreCase = true))
    }

    private fun getUrlRequestParams(url: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        try {
            val charset = "utf-8"
            val decodedUrl = URLDecoder.decode(url, charset)
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

}