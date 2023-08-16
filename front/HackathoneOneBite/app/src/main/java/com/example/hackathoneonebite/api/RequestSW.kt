package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.Body
import java.time.LocalDateTime

//Login화면
data class LoginSignInRequest( //최초 가입 시 정보 등록
    @SerializedName("username") val username: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("email") val email: String = ""
)
data class LoginCheckEmailExistRequest( //구글 로그인 시 계정 정보 존재 여부 확인
    @SerializedName("email")var email: String = ""
)

//메인화면
data class Main1LoadPostRequest(
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("id")var id: Long = 0,
    @SerializedName("lastPostDateStr")var lastPostDate: String,
    @SerializedName("limit")var limit: Int = 20
)

//댓글
data class CreateComment (
    @SerializedName("postId")var postId: Long,
    @SerializedName("authorId")var commentWriterId: Long,
    @SerializedName("content")var commentContent : String
)
//프로필 수정
data class UpdateProfileRequest (
    @SerializedName("userId")var userId: String,
    @SerializedName("username")var username: String
)