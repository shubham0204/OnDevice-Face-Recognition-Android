package com.ml.shubham0204.facenet_android.domain

import android.net.Uri
import com.ml.shubham0204.facenet_android.data.FaceImageRecord
import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.domain.embeddings.FaceNet
import com.ml.shubham0204.facenet_android.domain.face_detection.MLKitFaceDetector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageVectorUseCase
@Inject
constructor(
    private val mlKitFaceDetector: MLKitFaceDetector,
    private val imagesVectorDB: ImagesVectorDB,
    private val faceNet: FaceNet
) {

    suspend fun addImage(personID: Long, personName: String, imageUri: Uri): Result<Boolean> {
        val faceDetectionResult = mlKitFaceDetector.getCroppedFaces(imageUri)
        if (faceDetectionResult.isSuccess) {
            val embedding = faceNet.getFaceEmbedding(faceDetectionResult.getOrNull()!!)
            imagesVectorDB.addFaceImageRecord(
                FaceImageRecord(
                    personID = personID,
                    personName = personName,
                    faceEmbedding = embedding
                )
            )
            return Result.success(true)
        } else {
            return Result.failure(faceDetectionResult.exceptionOrNull()!!)
        }
    }

    suspend fun removeImages(personID: Long) {
        imagesVectorDB.removeFaceRecordsWithPersonID(personID)
    }
}
