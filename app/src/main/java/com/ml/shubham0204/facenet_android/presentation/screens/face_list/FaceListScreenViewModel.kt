package com.ml.shubham0204.facenet_android.presentation.screens.face_list

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FaceListScreenViewModel
@Inject
constructor(val imageVectorUseCase: ImageVectorUseCase, val personUseCase: PersonUseCase) :
    ViewModel() {

    val personFlow = personUseCase.getAll()

    fun removeFace(id: Long) {
        personUseCase.removePerson(id)
        imageVectorUseCase.removeImages(id)
    }
}
