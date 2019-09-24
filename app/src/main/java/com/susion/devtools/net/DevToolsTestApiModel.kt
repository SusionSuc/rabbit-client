package com.susion.devtools.net

import com.susion.devtools.net.entities.GameInfoListWrapper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * susionwang at 2019-09-24
 */
class DevToolsTestApiModel {

    fun getAllGameList(): Observable<GameInfoListWrapper> {
        return RetrofitClient.getApiService(ApiServices::class.java).requestGameList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    interface ApiServices{
        //获取所有游戏列表
        @Headers("urlname:api_takumi")
        @GET("apihub/api/getGameList")
        fun requestGameList(): Observable<GameInfoListWrapper>
    }

}