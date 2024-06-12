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
import kotlin.math.pow
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
        val faceDetectionResult = mlKitFaceDetector.getCroppedFace(frameBitmap)
        if (faceDetectionResult.isSuccess) {
            val embedding = faceNet.getFaceEmbedding(faceDetectionResult.getOrNull()!!)
            val recognitionResult = imagesVectorDB.getNearestEmbeddingPersonName(embedding) ?: return null
            val distance = cosineDistance(embedding, recognitionResult.faceEmbedding)
            return if (distance > 0.4) {
                recognitionResult.personName
            } else { "Not recognized" }
        } else {
            return null
        }
    }

    private fun cosineDistance(x1: FloatArray, x2: FloatArray): Float {
        var mag1 = 0.0f
        var mag2 = 0.0f
        var product = 0.0f
        for (i in x1.indices) {
            mag1 += x1[i].pow(2)
            mag2 += x2[i].pow(2)
            product += x1[i] * x2[i]
        }
        mag1 = sqrt( mag1 )
        mag2 = sqrt( mag2 )
        return product / (mag1 * mag2)
    }

    fun removeImages(personID: Long) {
        imagesVectorDB.removeFaceRecordsWithPersonID(personID)
    }
}