package com.example.hackathoneonebite.Data

import java.io.Serializable
import java.time.LocalDateTime
import com.google.gson.annotations.SerializedName


data class Post(
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