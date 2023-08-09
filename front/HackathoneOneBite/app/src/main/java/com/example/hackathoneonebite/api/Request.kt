package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class Main1LoadPostRequest(
    @SerializedName("theme")var theme: String = "0",
    @SerializedName("image")var image: MultipartBody.Part? = null
)