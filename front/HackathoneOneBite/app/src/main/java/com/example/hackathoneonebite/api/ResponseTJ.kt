package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName


data class Main3UploadPostIsComplete(
    @SerializedName("success")val success: Boolean //true이면 전달 잘됨
)