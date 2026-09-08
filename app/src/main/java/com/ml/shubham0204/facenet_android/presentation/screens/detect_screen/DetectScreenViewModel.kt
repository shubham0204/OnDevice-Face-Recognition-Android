package com.ml.shubham0204.facenet_android.presentation.screens.detect_screen

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ml.shubham0204.facenet_android.data.RecognitionMetrics
import com.ml.shubham0204.facenet_android.data.SettingsStore
import com.ml.shubham0204.facenet_android.domain.ImageVectorUseCase
import com.ml.shubham0204.facenet_android.domain.PersonUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetectScreenViewModel(
    val personUseCase: PersonUseCase,
    val imageVectorUseCase: ImageVectorUseCase,
    val settingsStore: SettingsStore
) : ViewModel() {
    private val KEY_SETTINGS_CAMERA_FACING = "camera_facing"
    private val CAMERA_FACING_VALUE_BACK = "back"
    private val CAMERA_FACING_VALUE_FRONT = "front"

    val RECOGNITION_MODE_REALTIME = 1
    val RECOGNITION_MODE_IMAGE = 2

    val faceDetectionMetricsState = mutableStateOf<RecognitionMetrics?>(null)
    val cameraFacing = mutableIntStateOf(getCameraFacing())
    val recognitionMode = mutableIntStateOf(RECOGNITION_MODE_REALTIME)

    fun getNumPeople(): Long = personUseCase.getCount()

    private fun getCameraFacing(): Int {
        val cameraFacing = settingsStore.get(KEY_SETTINGS_CAMERA_FACING)
        return if (cameraFacing == CAMERA_FACING_VALUE_FRONT) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    }

    private fun saveCameraFacingSetting(cameraFacing: Int) {
        settingsStore.save(
            KEY_SETTINGS_CAMERA_FACING,
            if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
                CAMERA_FACING_VALUE_FRONT
            } else {
                CAMERA_FACING_VALUE_BACK
            }
        )
    }

    fun changeCameraFacing() {
        if (cameraFacing.intValue == CameraSelector.LENS_FACING_FRONT) {
            cameraFacing.intValue = CameraSelector.LENS_FACING_BACK
        } else {
            cameraFacing.intValue = CameraSelector.LENS_FACING_FRONT
        }
        saveCameraFacingSetting(cameraFacing.intValue)
    }

    fun switchRecognitionMode() {
        if (recognitionMode.intValue == RECOGNITION_MODE_REALTIME) {
            recognitionMode.intValue = RECOGNITION_MODE_IMAGE
        } else {
            recognitionMode.intValue = RECOGNITION_MODE_REALTIME
        }

    }
}
