package com.example.hackathoneonebite.api

import com.example.hackathoneonebite.Data.User
import retrofit2.http.*
import retrofit2.Call

public interface API {
    //login
    @POST("/android")
    fun getLoginResponse(@Body user: User): Call<User>
}