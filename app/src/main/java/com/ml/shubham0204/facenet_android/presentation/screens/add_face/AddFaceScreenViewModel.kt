package com.ml.shubham0204.facenet_android.presentation.screens.add_face

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.domain.AppException
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.NativeFaceRecognitionModule
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import com.ml.shubham0204.facenet_android.presentation.components.setProgressDialogText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AddFaceScreenViewModel(
    private val context: Context,
    private val personUseCase: PersonUseCase,
    private val imageVectorUseCase: ImageVectorUseCase,
    private val nativeFaceRecognitionModule: NativeFaceRecognitionModule
) : ViewModel() {
    val personNameState: MutableState<String> = mutableStateOf("")
    val selectedImageURIs: MutableState<List<Uri>> = mutableStateOf(emptyList())

    val isProcessingImages: MutableState<Boolean> = mutableStateOf(false)
    val numImagesProcessed: MutableState<Int> = mutableIntStateOf(0)

    fun addImages() {
        isProcessingImages.value = true
        CoroutineScope(Dispatchers.Default).launch {
            val id =
                personUseCase.addPerson(
                    personNameState.value,
                    selectedImageURIs.value.size.toLong(),
                )
            val images = selectedImageURIs.value.map {
                val imageInputStream = context.contentResolver.openInputStream(it) ?: null
                val imageBitmap = BitmapFactory.decodeStream(imageInputStream)
                imageInputStream?.close()
                imageBitmap
            }
            nativeFaceRecognitionModule.insert(personNameState.value, images)
            numImagesProcessed.value += 1
            setProgressDialogText("Processed ${numImagesProcessed.value} image(s)")
            isProcessingImages.value = false
        }
    }
}
