package com.ml.shubham0204.facenet_android

import android.app.Application
import com.ml.shubham0204.facenet_android.data.ObjectBoxStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBoxStore.init(this)
    }

}
