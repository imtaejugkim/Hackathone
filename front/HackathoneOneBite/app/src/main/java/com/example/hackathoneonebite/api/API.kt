// API Interface
package com.example.hackathoneonebite.api

import com.example.hackathoneonebite.Data.Post
import com.example.hackathoneonebite.Data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.Call

public interface API {
    //로그인 시
    @POST("/api/user/check-email") //이메일 존재 여부 확인
    fun loginCheckEmailExistRequest(
        @Body request: LoginCheckEmailExistRequest
    ): Call<LoginCheckEmailExistResponse>

    @POST("/api/user/register") //최초 가입 시 화면
    fun loginSignInRequest(
        @Body request: LoginSignInRequest
    ): Call<LoginSignInResponse>

    //이미지 업로드
    @Multipart
    @POST("/api/upload/create")
    fun uploadPost(
        @Part images: ArrayList<MultipartBody.Part>,
        @Part("theme") theme: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("musicNum") musicNum: RequestBody?,
        @Part("text") message: RequestBody
    ): Call<Main3UploadPostIsComplete>

    //이미지 로드
    @GET("/api/posts/{postId}") //이미지 로드
    fun main1LoadPostRequest(
        @Query("id") id: Long,
        @Query("theme") theme: Int,
        @Query("lastPostDate") lastPostDate: String?,  // LocalDateTime을 문자열로 변환
        @Query("limit") limit: Int
    ): Call<Main1LoadPostResponse>
    @Multipart
    @POST("/api/load")
    fun main1LoadPost1231231(
        @Part("theme") theme: RequestBody, //Int
        @Part("user_id") user_id: RequestBody //String
    ): Call<Main1LoadPostResponse>

    //랭킹


    //프로필

}