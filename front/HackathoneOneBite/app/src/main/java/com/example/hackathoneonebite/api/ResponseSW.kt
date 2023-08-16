package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime


//Login화면
data class LoginSignInResponse(
    @SerializedName("success") val isSuccess: Boolean, //true면 성공.
    @SerializedName("id") val id: Long //최초 가입 시 생성된 아이디
)
data class LoginCheckEmailExistResponse(
    @SerializedName("exists")val isExist: Boolean, //true이면 email이 이미 존재함.
    @SerializedName("id")val id: Long, //exist false인 경우에는 -1을 담아준다.
    @SerializedName("userId")val userId: String
)

//메인화면
data class Main1LoadPostResponse(
    @SerializedName("id") val postId: Long,
    @SerializedName("images") val images: ArrayList<String>,
    @SerializedName("theme") val theme: Int,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("date") var date: String,
    @SerializedName("text") val text: String,
    @SerializedName("musicNum") val musicNum: Int,
    @SerializedName("participantUserIds") val participantUserIds: ArrayList<Long>,
    @SerializedName("likedByCurrentUser") val isLikeClicked: Boolean,
    @SerializedName("participantUserIdStrings") val participantUserIdStrings: ArrayList<String>,
    @SerializedName("participantsUserProfileUrl") val participantsUserProfileUrl: ArrayList<String>
)

//댓글 창
data class CommentResponse(
    @SerializedName("id") val commentId: Long,
    @SerializedName("postId") val postId: Long,
    @SerializedName("content") val text: String,
    @SerializedName("createdAt") val time: String,
    @SerializedName("authorId") val commentWriterId: Long,
    @SerializedName("authorUserIdString") val commentWriterUserId: String,
    @SerializedName("authorName") val commentWriterUsername: String,
    @SerializedName("profileUrl") val profileImageUrl: String
)

//좋아요
data class LikeClickResponse(
    @SerializedName("status") val afterStatus: Boolean,
    @SerializedName("message") val serverMessage: String
)

//팔로우
data class FollowToggleResponse(
    @SerializedName("isFollowing") val isFollowing: Boolean,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("message") val message: String
)

//프로필화면
data class Main5LoadProfileInfoResponse (
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("userId") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("userIdString") val userId: String,
    @SerializedName("email") val email: String,
    @SerializedName("createdPostIds") val postArray: ArrayList<Long>,
    @SerializedName("profilePictureUrl") val profileImageUrl: String,
    @SerializedName("backgroundPictureUrl") val backgroundImageUrl: String,
    @SerializedName("relayReceivedCount") val relayReceivedCount: Int,
    @SerializedName("followerIds") val followerIds: ArrayList<Long>,
    @SerializedName("followingIds") val followingIds: ArrayList<Long>,
    @SerializedName("score") val score: Int,
    @SerializedName("selfProfile") val selfProfile: Boolean,
    @SerializedName("editProfile") val editProfile: Boolean,
    @SerializedName("following") val following: Boolean
)
data class Main5LoadPostInfoResponse (
    @SerializedName("id") val postId: Long,
    @SerializedName("images") val imageArray: ArrayList<String>,
    @SerializedName("mainImage") val mainImage: String,
    @SerializedName("theme") val theme: Int,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("date") val date: String,
    @SerializedName("text") val text: String,
    @SerializedName("comments") val comments: String,
    @SerializedName("musicNum") val musicNum: Int,
    @SerializedName("participantUserIds") val participantUserIds: ArrayList<Long>,
    @SerializedName("likedByCurrentUser") val likedByCurrentUser: Boolean,
    @SerializedName("participantUserIdStrings") val participantUserIdStrings: ArrayList<String>,
    @SerializedName("participantsUserProfileUrl") val participantsUserProfileUrl: ArrayList<String>
)
//프로필 수정
data class UpdateProfileResponse (
    @SerializedName("success") val isSuccess: Boolean = true,
    @SerializedName("message") val message: String
)
//포스트 하나 정보 가져오기
data class LoadAPostInfoResponse (
    @SerializedName("id") val id: Long,
    @SerializedName("images") val imageArray: List<String>,
    @SerializedName("mainImage") val mainImage: String,
    @SerializedName("theme") val theme: Int,
    @SerializedName("likeCount") val likeCount: Int,
    @SerializedName("date") val date: String,
    @SerializedName("text") val text: String,
    @SerializedName("comments") val comments: List<CommentResponse>,
    @SerializedName("musicNum") val musicNum: Int,
    @SerializedName("participantUserIds") val participantUserIds: List<Long>,
    @SerializedName("likedByCurrentUser") var likeClicked:Boolean,
    @SerializedName("participantUserIdStrings") val participantUserIdStrings: List<String>,
    @SerializedName("participantsUserProfileUrl") val participantsUserProfileUrl: List<String>
)
//랭킹
data class LoadRanking (
    @SerializedName("userId") val userId: Long,
    @SerializedName("userIdStr") val userIdStr: String,
    @SerializedName("username") val username: String,
    @SerializedName("score") val score: Int,
    @SerializedName("profileUrl") val profileUrl: String
)