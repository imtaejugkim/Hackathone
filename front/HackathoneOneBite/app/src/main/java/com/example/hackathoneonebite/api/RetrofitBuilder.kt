package com.example.hackathoneonebite.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api: API
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://203.252.139.231:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(API::class.java)
    }
}