package com.ml.shubham0204.facenet_android.domain

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import com.ml.shubham0204.facenet_android.data.FaceImageRecord
import com.ml.shubham0204.facenet_android.data.ImagesVectorDB
import com.ml.shubham0204.facenet_android.domain.embeddings.FaceNet
import com.ml.shubham0204.facenet_android.domain.face_detection.MediapipeFaceDetector
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.sqrt

@Singleton
class ImageVectorUseCase
@Inject
constructor(
    private val mediapipeFaceDetector: MediapipeFaceDetector,
    private val imagesVectorDB: ImagesVectorDB,
    private val faceNet: FaceNet
) {

    // Add the person's image to the database
    suspend fun addImage(personID: Long, personName: String, imageUri: Uri): Result<Boolean> {
        // Perform face-detection and get the cropped face as a Bitmap
        val faceDetectionResult = mediapipeFaceDetector.getCroppedFace(imageUri)
        if (faceDetectionResult.isSuccess) {
            // Get the embedding for the cropped face, and store it
            // in the database, along with `personId` and `personName`
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

    // From the given frame, return the name of the person by performing
    // face recognition
    suspend fun getNearestPersonName(frameBitmap: Bitmap): List<Pair<String, Rect>> {
        // Perform face-detection and get the cropped face as a Bitmap
        val faceDetectionResult = mediapipeFaceDetector.getAllCroppedFaces(frameBitmap)
        val faceRecognitionResults = ArrayList<Pair<String, Rect>>()
        for (result in faceDetectionResult) {
            // Get the embedding for the cropped face (query embedding)
            val (croppedBitmap, boundingBox) = result
            val embedding = faceNet.getFaceEmbedding(croppedBitmap)
            // Perform nearest-neighbor search
            val recognitionResult = imagesVectorDB.getNearestEmbeddingPersonName(embedding)
            if (recognitionResult == null) {
                faceRecognitionResults.add(Pair("Not recognized", boundingBox))
                continue
            }
            // Calculate cosine similarity between the nearest-neighbor
            // and the query embedding
            val distance = cosineDistance(embedding, recognitionResult.faceEmbedding)
            // If the distance > 0.4, we recognize the person
            // else we conclude that the face does not match enough
            if (distance > 0.4) {
                faceRecognitionResults.add(Pair(recognitionResult.personName, boundingBox))
            } else {
                faceRecognitionResults.add(Pair("Not recognized", boundingBox))
            }
        }
        return faceRecognitionResults
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
        mag1 = sqrt(mag1)
        mag2 = sqrt(mag2)
        return product / (mag1 * mag2)
    }

    fun removeImages(personID: Long) {
        imagesVectorDB.removeFaceRecordsWithPersonID(personID)
    }
}
