package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class Main3UploadPostRequest(
    @SerializedName("imgArray")var imgArray: ArrayList<MultipartBody.Part> =ArrayList(),
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("user_id")var user_id: String = "",
    @SerializedName("music_num")var music_num: String = "",
    @SerializedName("text")var message: String = ""
)

data class Main3RelayPostRequest(
    @SerializedName("imgArray")var imgArray: ArrayList<MultipartBody.Part> =ArrayList(),
    @SerializedName("theme")var theme: Int = 0,
    @SerializedName("user_id")var user_id: String = "",
    @SerializedName("music_num")var music_num: String = "",
    @SerializedName("text")var message: String = ""
)

data class Main3RelaySearchRequest(
    @SerializedName("id")var id: Long = 0,
    @SerializedName("username")var username: String = "",
    @SerializedName("user_id")var user_id: String = "",
    @SerializedName("email")var email: String = "",
)