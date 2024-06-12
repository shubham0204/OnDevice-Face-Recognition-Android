package com.ml.shubham0204.facenet_android.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FaceListScreenViewModel
@Inject
constructor(
    val personUseCase: PersonUseCase
): ViewModel() {

    val personFlow = personUseCase.getAll()

    fun removeFace(id: Long) {
        personUseCase.removePerson(id)
    }

}
