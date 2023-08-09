package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

//Login화면
data class LoginSignInRequest( //최초 가입 시 정보 등록
    @SerializedName("username") val username: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("email") val email: String = ""
)
data class LoginCheckEmailExistRequest( //구글 로그인 시 계정 정보 존재 여부 확인
    @SerializedName("email")var email: String = ""
)

//메인화면
data class Main1LoadPostRequest(
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("image")var image: MultipartBody.Part? = null
)