package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetectScreenViewModel
@Inject
constructor(val personUseCase: PersonUseCase, val imageVectorUseCase: ImageVectorUseCase) :
    ViewModel() {

    val faceDetectionMetricsState = mutableStateOf<RecognitionMetrics?>(null)

    fun getNumPeople(): Long = personUseCase.getCount()

}
