package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName


data class Main3UploadPostIsComplete(
    @SerializedName("success")val success: Boolean //true이면 전달 잘됨
)

data class Main3RelayPostIsComplete(
    @SerializedName("success")val success: Boolean //true면 릴레이 전달 성공

    )

data class Main3AddPostIsComplete(
    @SerializedName("success")val success: Boolean //true면 릴레이 전달 성공

)

data class NotificationLoadResponse(
    @SerializedName("id") val id : Long,
    @SerializedName("message") val message : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("userId") val userId : String,
    @SerializedName("userName") val userName : String,
    @SerializedName("remainingTime") val remainingTime : String,
    @SerializedName("postId") val postId : Long

    )

data class Main3RelaySearchResponse(
    @SerializedName("id")var id: Long = 0,
    @SerializedName("username")var username: String = "",
    @SerializedName("userId")var userId: String = "",
    @SerializedName("email")var email: String = "",
)

data class Main3RelayPostResponse(
    @SerializedName("id")var id: Long = 0,
    @SerializedName("images")var images: ArrayList<String>,
    @SerializedName("mainImage")var mainImage: String = "",
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("date")var date: String = "",
    @SerializedName("text")var text: String = "",
    @SerializedName("comments")var comments: String = "",
    @SerializedName("musicNum")var musicNum: Int = 0,
    @SerializedName("participantUserIds") val participantUserIds: ArrayList<Long>,
    @SerializedName("likedByCurrentUser") val likedByCurrentUser: Boolean,
    @SerializedName("participantUserIdStrings") val participantUserIdStrings: ArrayList<String>,
    @SerializedName("participantsUserProfileUrl") val participantsUserProfileUrl: ArrayList<String>
    )

