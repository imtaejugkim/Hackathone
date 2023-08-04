package com.example.hackathoneonebite.Data

import android.graphics.Bitmap
import java.time.LocalDate

data class Post(
    var imgArray: Array<Int> = Array(4) { 0 },
    var id: String? = null,
    var likeCount: Int = 0,
    var date: LocalDate? = null,
    var message: String? = null,
    var frame: String? = null
)

