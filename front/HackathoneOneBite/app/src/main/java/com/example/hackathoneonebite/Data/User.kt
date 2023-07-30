package com.example.hackathoneonebite.Data

import com.google.gson.annotations.SerializedName

class User (
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("password")
    var pw: String? = null
)