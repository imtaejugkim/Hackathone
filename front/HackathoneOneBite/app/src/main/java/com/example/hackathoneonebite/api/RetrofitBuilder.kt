package com.example.hackathoneonebite.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api: API
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://23.12.123.12/android/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(API::class.java)
    }
}