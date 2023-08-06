package com.mytest.mise.network.service

import com.mytest.mise.network.dto.AirQualityRes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApi {

    @GET("nearest_city") //BASE_URL + "/nearest_city" 이므로
    fun getAirQuality(@Query("lat") latitude : Double, @Query("lon") longitude : Double, @Query("key") key : String) : Call<AirQualityRes>
}