package com.example.hackathoneonebite.api

import com.google.gson.annotations.SerializedName

data class Main1LoadPostResponse(
    @SerializedName("text") val text: String,
    @SerializedName("date") val date: String,
    @SerializedName("images") val images: ArrayList<String>
)
