package com.example.hackathoneonebite.Data

import java.io.Serializable
import java.time.LocalDateTime
import com.google.gson.annotations.SerializedName


data class Post(
    var imgArray: Array<String> = Array(4) {""}, // 이미지 URI 또는 파일 경로의 배열

    var theme: Int = 0,

    var userId: String? = "",

    var likeCount: Int = 0,

    var date: LocalDateTime? = null,

    var message: String? = null,

    var isFliped: Boolean = false,

    var musicNum: Int = -1,

    var postId: Long = 0,

    var likeClicked: Boolean = false,

    var participantUserIds: ArrayList<Long> = arrayListOf()
) : Serializable