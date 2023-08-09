package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


//Login화면
data class LoginSignInResponse(
    @SerializedName("success") val isSuccess: Boolean //true면 성공.
)
data class LoginCheckEmailExistResponse(
    @SerializedName("exists")val isExist: Boolean //true이면 email이 이미 존재함.
)

//메인화면
data class Main1LoadPostResponse(
    @SerializedName("text") val text: String,
    @SerializedName("date") val date: String,
    @SerializedName("images") val images: ArrayList<String>
)