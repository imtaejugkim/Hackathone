package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


//Login화면
data class LoginSignInResponse(
    @SerializedName("success") val isSuccess: Boolean, //true면 성공.
    @SerializedName("id") val id: Long //최초 가입 시 생성된 아이디
)
data class LoginCheckEmailExistResponse(
    @SerializedName("exist")val isExist: Boolean, //true이면 email이 이미 존재함.
    @SerializedName("id")val id: Long //exist false인 경우에는 -1을 담아준다.
)

//메인화면
data class Main1LoadPostResponse(
    @SerializedName("id") val postId: Long,
    @SerializedName("images") val images: ArrayList<String>,
    @SerializedName("theme") val theme: Int,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("date") val date: String,
    @SerializedName("text") val text: String,
    @SerializedName("musicNum") val musicNum: Int
)