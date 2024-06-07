package com.ml.shubham0204.facenet_android.domain

import android.graphics.Bitmap
import com.ml.shubham0204.facenet_android.data.FaceImageRecord
import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.domain.embeddings.FaceNet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageVectorUseCase
@Inject
constructor(
    private val imagesVectorDB: ImagesVectorDB,
    private val faceNet: FaceNet
) {

    suspend fun addImage(
        personID: Long,
        personName: String,
        croppedFaceImage: Bitmap
    ) {
        val embedding = faceNet.getFaceEmbedding(croppedFaceImage)
        imagesVectorDB.addFaceImageRecord(
            FaceImageRecord(
                personID = personID,
                personName = personName,
                faceEmbedding = embedding
            )
        )
    }

    suspend fun removeImages(personID: Long) {
        imagesVectorDB.removeFaceRecordsWithPersonID(personID)
    }

}
