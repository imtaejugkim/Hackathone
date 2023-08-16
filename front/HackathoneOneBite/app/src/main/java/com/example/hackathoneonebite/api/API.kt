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
        @Part("postId") postId: RequestBody,
        @Part images: ArrayList<MultipartBody.Part>,
        @Part("userId") userId: RequestBody,
    ): Call<Main3AddPostIsComplete>

    // 릴레이 업로드
    @Multipart
    @POST("/api/upload/relay")
    fun relayPost(
        @Part("postId") postId: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part("remainingSeconds") remainingSeconds : RequestBody
    ): Call<Main3RelayPostIsComplete>

    //알림 로드
    @GET("/api/notifications/{userId}")
    fun notificationLoadRequest(
        @Path("userId") id: Long
    ): Call<List<NotificationLoadResponse>>

    // 릴레이 게시물 로드
    @GET("/api/posts/{postId}")
    fun main3LoadRelayPostRequest(
        @Path("postId") postId: Long
    ): Call<Main3RelayPostResponse>

    //검색 로드
    @GET("/api/user/search")
    fun main3LoadUserRequest(
        @Query("Id") id: Long,
        @Query("q") q : String
    ) : Call<List<Main3RelaySearchResponse>>

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
    @Multipart
    @POST("/api/user/update/{id}")
    fun updateProfile_userId_username(
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("username") username: RequestBody
    ): Call<UpdateProfileResponse>
    @Multipart
    @POST("/api/user/update/{userId}")
    fun updateProfile_userId_username_profileImg(
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("username") username: RequestBody,
        @Part("profileImage") profileImage: RequestBody
    ): Call<UpdateProfileResponse>
    @Multipart
    @POST("/api/user/update/{userId}")
    fun updateProfile_userId_username_backgroundImg(
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("username") username: RequestBody,
        @Part("backgroundImage") backgroundImage: RequestBody
    ): Call<UpdateProfileResponse>
    @Multipart
    @POST("/api/user/update/{userId}")
    fun updateProfile_userId_username_profileImg_backgroundImg(
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part("username") username: RequestBody,
        @Part("profileImage") profileImage: RequestBody,
        @Part("backgroundImage") backgroundImage: RequestBody
    ): Call<UpdateProfileResponse>


    //랭킹
    @GET("/api/user/ranking")
    fun main4Ranking(): Call<List<LoadRanking>>

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
    //포스트 하나 받아오기
    @GET("/api/posts/{postId}")
    fun loadAPostInfoRequest (
        @Path("postId") postId: Long
    ): Call<LoadAPostInfoResponse>
}