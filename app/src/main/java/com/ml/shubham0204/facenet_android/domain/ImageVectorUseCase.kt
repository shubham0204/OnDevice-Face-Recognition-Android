package com.ml.shubham0204.facenet_android.domain

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
import com.ml.shubham0204.facenet_android.domain.face_detection.FaceSpoofDetector
import org.koin.core.annotation.Single
import kotlin.time.measureTimedValue

@Single
class ImageVectorUseCase(
    private val faceSpoofDetector: FaceSpoofDetector,
    private val nativeFaceRecognitionModule: NativeFaceRecognitionModule
) {
    data class FaceRecognitionResult(
        val personName: String,
        val boundingBox: Rect,
        val spoofResult: FaceSpoofDetector.FaceSpoofResult? = null,
    )

    suspend fun getNearestPersonName(
        frameBitmap: Bitmap
    ): Pair<RecognitionMetrics?, List<FaceRecognitionResult>> {
        val (results, time) = measureTimedValue { nativeFaceRecognitionModule.recognize(frameBitmap) }
        Log.e("APP", "Time taken is ${time.inWholeMilliseconds}, ${results.firstOrNull()?.cosineSimilarity}")
        val faceRecognitionResults = results.map {
            FaceRecognitionResult(
                it.personName,
                it.faceBoundingBox.toRect(),
                faceSpoofDetector.detectSpoof(frameBitmap, it.faceBoundingBox.toRect())
            )
        }
        return Pair(RecognitionMetrics(0L, 0L, 0L, 0L), faceRecognitionResults)
    }

    fun removeImages(name: String) {
        val removed = nativeFaceRecognitionModule.remove(name)
        Log.e("APP", "Removed: $removed")
    }
}
