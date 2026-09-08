package com.ml.shubham0204.facenet_android.di

import android.content.Context
import com.ml.shubham0204.facenet_android.domain.NativeFaceRecognitionModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.io.File

@Module
@ComponentScan("com.ml.shubham0204.facenet_android")
class AppModule {
    @Single
    fun provideNativeFaceRecognitionModule(context: Context): NativeFaceRecognitionModule {
        val dbPath = File(context.filesDir, "vectordb.bin").absolutePath
        val module = NativeFaceRecognitionModule()
        module.createFaceRecognizer(
            dbPath,
            "/data/local/tmp/qfacenet.pte"
        )
        return module
    }
}
