package com.ml.shubham0204.facenet_android.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.VectorDistanceType

@Entity
data class FaceImageRecord(
    @Id var recordID: Long = 0,
    @Index var personID: Long = 0,
    var personName: String = "",
    @HnswIndex(dimensions = 512, distanceType = VectorDistanceType.COSINE) var faceEmbedding: FloatArray = floatArrayOf()
)

@Entity
data class PersonRecord(
    @Id var personID: Long = 0,
    var personName: String = "",
    var numImages: Long = 0,
    var addTime: Long = 0
)
