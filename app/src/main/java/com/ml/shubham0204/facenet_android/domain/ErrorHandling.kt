package com.ml.shubham0204.facenet_android.domain

enum class ErrorCode(val message: String) {
    MULTIPLE_FACES("Multiple faces found in the image"),
    NO_FACE("No faces were in the image"),
    FACE_DETECTOR_FAILURE("Face detection failed")
}

class AppException(val errorCode: ErrorCode) : Exception()
