package com.example.hackathoneonebite.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api: API
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://221.146.39.177:8081/connection/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(API::class.java)
    }
}