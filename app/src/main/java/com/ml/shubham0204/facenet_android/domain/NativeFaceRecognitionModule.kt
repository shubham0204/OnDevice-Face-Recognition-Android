package com.ml.shubham0204.facenet_android.domain

import android.graphics.Bitmap
import android.graphics.Rect
import dalvik.annotation.optimization.FastNative

class NativeFaceRecognitionModule {

    data class FaceBoundingBox(
        val top: Long,
        val left: Long,
        val right: Long,
        val bottom: Long
    ) {
        fun toRect(): Rect {
            return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
    }

    data class NNQueryResult(
        val personName: String,
        val cosineSimilarity: Double,
        val faceBoundingBox: FaceBoundingBox
    )

    companion object {
        init {
            System.loadLibrary("facenet_vectorsearch_jni")
        }
    }

    external fun createFaceRecognizer(dbPath: String, faceNetModelPath: String)

    external fun insert(personName: String, images: List<Bitmap>)

    external fun recognize(image: Bitmap): List<NNQueryResult>

    external fun remove(personName: String): Boolean

    external fun clear()
}