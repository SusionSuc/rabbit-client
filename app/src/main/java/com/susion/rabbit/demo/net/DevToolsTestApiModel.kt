package com.susion.rabbit.demo.net

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET

/**
 * susionwang at 2019-09-24
 */
class DevToolsTestApiModel {

    fun getAllGameList(): Observable<Any> {
        return RetrofitClient.getApiService(ApiServices::class.java)
            .requestTestList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    interface ApiServices{
        @GET("/wxarticle/chapters/json")
        fun requestTestList(): Observable<Any>
    }

}