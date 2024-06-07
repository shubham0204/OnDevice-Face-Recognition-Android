package com.ml.shubham0204.facenet_android.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class FaceImageRecord(
    @Id var recordId: Long = 0,
    @Index var personId: Long = 0,
    var personName: String = "",
    @HnswIndex(dimensions = 128) var faceEmbedding: FloatArray = floatArrayOf()
)

@Entity
data class PersonRecord(
    @Id var personId: Long = 0,
    var personName: String = "",
    var numImages: Long = 0
)