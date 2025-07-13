package com.ml.shubham0204.facenet_android.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.VectorDistanceType

@Entity
data class FaceImageRecord(
    // primary-key of `FaceImageRecord`
    @Id var recordID: Long = 0,
    // personId is derived from `PersonRecord`
    @Index var personID: Long = 0,
    var personName: String = "",
    // the FaceNet-512 model provides a 512-dimensional embedding
    // the FaceNet model provides a 128-dimensional embedding
    @HnswIndex(
        dimensions = 512,
        distanceType = VectorDistanceType.COSINE,
    ) var faceEmbedding: FloatArray = floatArrayOf(),
)

@Entity
data class PersonRecord(
    // primary-key
    @Id var personID: Long = 0,
    var personName: String = "",
    // number of images selected by the user
    // under the name of the person
    var numImages: Long = 0,
    // time when the record was added
    var addTime: Long = 0,
)

data class RecognitionMetrics(
    val timeFaceDetection: Long,
    val timeVectorSearch: Long,
    val timeFaceEmbedding: Long,
    val timeFaceSpoofDetection: Long,
)
