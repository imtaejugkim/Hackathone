package com.example.hackathoneonebite.Data

import java.io.Serializable
import java.time.LocalDateTime
import com.google.gson.annotations.SerializedName


data class Post(
    @SerializedName("imgArray")
    var imgArray: Array<String> = Array(4) {""}, // 이미지 URI 또는 파일 경로의 배열

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("likeCount")
    var likeCount: Int = 0,

    @SerializedName("date")
    var date: LocalDateTime? = null,

    @SerializedName("message")
    var message: String? = null,

    @SerializedName("frame")
    var frame: String? = null,

    @SerializedName("isFliped")
    var isFliped: Boolean = false
) : Serializable