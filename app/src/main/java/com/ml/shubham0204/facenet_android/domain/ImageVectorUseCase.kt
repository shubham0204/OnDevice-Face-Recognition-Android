package com.ml.shubham0204.facenet_android.domain

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.ml.shubham0204.facenet_android.data.FaceImageRecord
import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.domain.embeddings.FaceNet
import com.ml.shubham0204.facenet_android.domain.face_detection.MLKitFaceDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class ImageVectorUseCase
@Inject
constructor(
    private val mlKitFaceDetector: MLKitFaceDetector,
    private val imagesVectorDB: ImagesVectorDB,
    private val faceNet: FaceNet
) {

    suspend fun addImage(personID: Long, personName: String, imageUri: Uri): Result<Boolean> {
        val faceDetectionResult = mlKitFaceDetector.getCroppedFace(imageUri)
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

    suspend fun getNearestPersonName(frameBitmap: Bitmap): String? {
        val t2 = System.currentTimeMillis()
        val faceDetectionResult = mlKitFaceDetector.getCroppedFace(frameBitmap)
        Log.e( "APP" , "Face detection: ${System.currentTimeMillis() - t2}")
        if (faceDetectionResult.isSuccess) {
            val t3 = System.currentTimeMillis()
            val embedding = faceNet.getFaceEmbedding(faceDetectionResult.getOrNull()!!)
            Log.e( "APP" , "embedding: ${System.currentTimeMillis() - t3}")
            val t1 = System.currentTimeMillis()
            val recognitionResult = imagesVectorDB.getNearestEmbeddingPersonName(embedding)
            Log.e( "APP" , "Vector DB time: ${System.currentTimeMillis() - t1}")
            Log.e( "APP" , "Distance: ${recognitionResult.first}")
            return if (recognitionResult.first > 0.6) {
                recognitionResult.second.personName
            } else { null }
        } else {
            return null
        }
    }

    fun removeImages(personID: Long) {
        imagesVectorDB.removeFaceRecordsWithPersonID(personID)
    }
}
