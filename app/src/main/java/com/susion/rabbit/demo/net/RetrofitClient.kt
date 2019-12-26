package com.susion.rabbit.demo.net

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.susion.rabbit.Rabbit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *  author: xun.wang on 2019/4/8
 **/
class RetrofitClient {

    var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit?
    private val DEFAULT_TIMEOUT: Long = 2
    private val url = "https://wanandroid.com"

    init {
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(Rabbit.getNetInterceptor())
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder().client(okHttpClient!!)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
    }

    companion object {
        private var instance: RetrofitClient? = null

        private fun getInstance(): RetrofitClient {
            if (instance == null) {
                synchronized(RetrofitClient::class) {
                    if (instance == null) {
                        instance =
                            RetrofitClient()
                    }
                }
            }
            return instance!!
        }

        fun <T> getApiService(className: Class<T>): T {
            val retrofitClient = getInstance()
            return retrofitClient.create(className)
        }
    }

    fun <T> create(service: Class<T>?): T {
        if (service == null || retrofit == null) {
            throw RuntimeException("Api service is null!")
        }
        return retrofit!!.create(service)
    }

}