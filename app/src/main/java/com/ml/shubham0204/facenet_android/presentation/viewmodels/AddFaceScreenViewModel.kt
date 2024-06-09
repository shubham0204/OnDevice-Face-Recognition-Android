package com.ml.shubham0204.facenet_android.presentation.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.domain.AppException
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AddFaceScreenViewModel
@Inject
constructor(val personUseCase: PersonUseCase, val imageVectorUseCase: ImageVectorUseCase) :
    ViewModel() {

    val personNameState: MutableState<String> = mutableStateOf("")
    val selectedImageURIs: MutableState<List<Uri>> = mutableStateOf(emptyList())

    val isProcessingImages: MutableState<Boolean> = mutableStateOf(false)
    val numImagesProcessed: MutableState<Int> = mutableIntStateOf(0)

    fun addImages() {
        val id =
            personUseCase.addPerson(personNameState.value, selectedImageURIs.value.size.toLong())
        isProcessingImages.value = true
        CoroutineScope(Dispatchers.Default).launch {
            selectedImageURIs.value.forEach {
                imageVectorUseCase.addImage(id, personNameState.value, it).onFailure {
                    val errorMessage = (it as AppException).errorCode.message
                    // TODO: Show message for each error here
                }
                numImagesProcessed.value += 1
            }
        }
    }
}
