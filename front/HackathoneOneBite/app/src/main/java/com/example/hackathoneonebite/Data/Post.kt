package com.example.hackathoneonebite.Data

import java.io.Serializable
import java.time.LocalDateTime
import com.google.gson.annotations.SerializedName


data class Post(
    @SerializedName("images")
    var imgArray: Array<String> = Array(4) {""}, // 이미지 URI 또는 파일 경로의 배열

    @SerializedName("theme")
    var theme: Int = 0,

    @SerializedName("id")
    var id: Long? = null,

    @SerializedName("likes")
    var likeCount: Int = 0,

    @SerializedName("createdAt")
    var date: LocalDateTime? = null,

    @SerializedName("text")
    var message: String? = null,

    @SerializedName("isFliped")
    var isFliped: Boolean = false
) : Serializable