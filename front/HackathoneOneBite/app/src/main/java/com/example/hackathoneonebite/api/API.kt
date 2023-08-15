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

    @Multipart
    @POST("/api/upload/add")
    fun addPost(
        @Part("postId") postId: Long,
        @Part images: ArrayList<MultipartBody.Part>,
        @Part("userId") userId: String,
    )

    // 릴레이 업로드
    @POST("/api/upload/relay")
    fun relayPost(
        @Part("postId") postId: Long,
        @Part("userId") userId: String,
        @Part("remainingSeconds") remainingSeconds : Int
    ): Call<Main3RelayPostIsComplete>


    //검색 로드
    @GET("/api/user/search")
    fun main3LoadUserRequest(
        @Path("Id") id: Long
    ) : Call<Main3RelaySearchRequest>

    //이미지 로드
    @GET("/api/posts/loadMain") //이미지 로드
    fun main1LoadPostRequest(
        @Query("userId") id: Long,
        @Query("theme") theme: Int,
        @Query("lastPostDate") lastPostDate: String?/*,  // LocalDateTime을 문자열로 변환
        @Query("limit") limit: Int*/
    ): Call<List<Main1LoadPostResponse>>
    //댓글
    //댓글 로드
    @GET("/api/comments/post/{postId}")
    fun loadComments(
        @Path("postId") postId: Long
    ): Call<List<CommentResponse>>
    //댓글 추가
    @POST("/api/comments/")
    fun createComment(
        @Body request: CreateComment
    ): Call<CommentResponse>
    //좋아요
    @POST("api/posts/{postId}/like")
    fun likeClickRequest(
        @Path("postId") postId: Long,
        @Query("userId") userId: Long
    ): Call<LikeClickResponse>
    //팔로우
    @POST("api/follow/toggle/{requesterId}/{targetId}")
    fun followToggleRequest(
        @Path("requesterId") requesterId: Long,
        @Path("targetId") targetId: Long
    ): Call<FollowToggleResponse>
    //프로필 수정
    /*@POST("/api/user/update/{userId}")
    fun updateProfile(

    )*/


    //랭킹


    //프로필
    //프로필 정보 받아오기
    @GET("/api/user/{targetUserId}/{currentUserId}")
    fun main5LoadProfileInfo (
        @Path("targetUserId") targetId: Long,
        @Path("currentUserId") currentId: Long
    ): Call<Main5LoadProfileInfoResponse>
    //포스트 정보 전부 받아오기
    @GET("/api/posts/user/{userId}")
    fun main5LoadPostInfo (
        @Path("userId") postId: Long,
        @Query("currentUser") currentUser: Long
    ): Call<List<Main5LoadPostInfoResponse>>
}