package com.example.hackathoneonebite.Data

import android.graphics.Bitmap
import java.time.LocalDate

data class Post(
    //TODO: 지금은 테스트를 위해 Array 자료형으로 Int가 들어가 있지만, 수정해야됨.
    var imgArray: Array<Int> = Array(4) { 0 },
    var id: String? = null,
    var likeCount: Int = 0,
    var date: LocalDate? = null,
    var message: String? = null,
    var frame: String? = null,
    var isFliped: Boolean = false
)

