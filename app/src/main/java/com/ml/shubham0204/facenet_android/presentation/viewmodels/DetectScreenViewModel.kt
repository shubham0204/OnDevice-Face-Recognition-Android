package com.ml.shubham0204.facenet_android.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetectScreenViewModel
@Inject
constructor(val personUseCase: PersonUseCase, val imageVectorUseCase: ImageVectorUseCase) :
    ViewModel() {

    suspend fun detectFace(frameBitmap: Bitmap): String {
        return imageVectorUseCase.getNearestPersonName(frameBitmap) ?: "Not recognized"
    }

    fun getNumPeople(): Long = personUseCase.getCount()
}
