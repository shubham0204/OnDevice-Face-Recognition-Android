package com.ml.shubham0204.facenet_android.di

import android.app.Application
import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.data.PersonDB
import com.ml.shubham0204.facenet_android.domain.embeddings.FaceNet
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// AppModule provides dependencies that are to be injected by Hilt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // SingletonComponent ensures that instances survive
    // across the application's lifespan
    // @Singleton creates a single instance in the app's lifespan

    @Provides
    @Singleton
    fun provideImageVectorDB(): ImagesVectorDB {
        return ImagesVectorDB()
    }

    @Provides
    @Singleton
    fun providePersonDB(): PersonDB {
        return PersonDB()
    }

    @Provides
    @Singleton
    fun provideFaceNetEmbeddingModel(context: Application): FaceNet {
        return FaceNet(context)
    }

}
