package com.example.hackathoneonebite

import android.app.Application

class MyApplication : Application() {

    companion object {
        var imageByteArrays: ArrayList<ByteArray> = ArrayList()
    }

    override fun onCreate() {
        super.onCreate()
        // 초기화나 다른 작업이 필요하다면 여기에서 수행할 수 있습니다.
    }
}