package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class Main1LoadPostRequest(
    @SerializedName("theme")var theme: String = "0",
    @SerializedName("image")var image: MultipartBody.Part? = null
)

data class Main3UploadPostRequest(
    @SerializedName("imgArray")var imgArray: ArrayList<MultipartBody.Part> =ArrayList(),
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("user_id")var user_id: String = "",
    @SerializedName("music_num")var music_num: String = "",
    @SerializedName("text")var message: String = ""
)