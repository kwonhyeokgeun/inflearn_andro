package com.mytest.mise.network.retrofit.connection

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    companion object{
        private const val BASE_URL = "https://api.airvisual.com/v2/"
        private var instance : Retrofit? = null
        fun getInstance() : Retrofit{
            if(instance==null){
                instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return instance!!
        }
    }
}