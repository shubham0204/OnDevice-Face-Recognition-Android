package com.ml.shubham0204.facenet_android.di

import android.content.Context
import com.ml.shubham0204.facenet_android.domain.face_detection.BaseFaceDetector
import com.ml.shubham0204.facenet_android.domain.face_detection.MLKitFaceDetector
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.ml.shubham0204.facenet_android")
class AppModule {

    @Single
    fun provideFaceDetector(context: Context): BaseFaceDetector = MLKitFaceDetector(context)
}
