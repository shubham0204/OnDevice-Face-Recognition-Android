package com.ml.shubham0204.facenet_android.di

import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.data.PersonDB
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
}
