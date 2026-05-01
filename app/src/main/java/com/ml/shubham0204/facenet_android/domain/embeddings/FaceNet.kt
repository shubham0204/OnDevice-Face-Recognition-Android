package com.ml.shubham0204.facenet_android.domain.embeddings

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.pytorch.executorch.EValue
import org.pytorch.executorch.Module
import org.pytorch.executorch.Tensor
import java.io.File
import java.io.FileOutputStream

// Derived from the original project:
// https://github.com/shubham0204/FaceRecognition_With_FaceNet_Android/blob/master/app/src/main/java/com/ml/quaterion/facenetdetection/model/FaceNetModel.kt
// Utility class for FaceNet model
@Single
class FaceNet(val context: Context) {
    // Input image size for FaceNet model.
    private val imgSize = 160L

    private var module: Module

    /**
     * NOTE:
     * Download the model from here:
     * https://huggingface.co/shubhxm0204/facenet-executorch/blob/main/vggface2-inception-resnetv1-xnnpack-fp32
     *
     * set the correct name below in the argument of copyAndReturnPath()
     * and place it in the assets folder
     */
    init {
        module = Module.load(copyAndReturnPath("model.pte"))
    }

    // Gets an face embedding using FaceNet
    suspend fun getFaceEmbedding(image: Bitmap) =
        withContext(Dispatchers.Default) {
            return@withContext runFaceNet(convertBitmapToBuffer(image))[0]
        }

    // Run the FaceNet model
    private fun runFaceNet(inputs: FloatArray): Array<FloatArray> {
        val imageTensor = Tensor.fromBlob(
            inputs,
            longArrayOf(1, imgSize, imgSize, 3)
        )
        val outputTensor = module.forward(EValue.from(imageTensor))[0].toTensor()
        val embedding = outputTensor.dataAsFloatArray
        return arrayOf(embedding)
    }

    // Resize the given bitmap and convert it to a ByteBuffer
    private fun convertBitmapToBuffer(image: Bitmap): FloatArray {
        val resizedBitmap = image.scale(160, 160, true)
        val pixels = IntArray(160 * 160)
        resizedBitmap.getPixels(pixels, 0, 160, 0, 0, 160, 160)
        val floats = FloatArray(160 * 160 * 3)
        var i = 0
        for (p in pixels) {
            floats[i++] = ((p shr 16) and 0xFF).toFloat()  // R
            floats[i++] = ((p shr 8) and 0xFF).toFloat()   // G
            floats[i++] = (p and 0xFF).toFloat()           // B
        }
        return floats
    }

    // Copy the file from the assets to the app's internal/private storage
    // and return its absolute path
    private fun copyAndReturnPath(assetsFilepath: String): String {
        val storageFile = File(context.filesDir, assetsFilepath)
        if (!storageFile.exists()) {
            storageFile.parentFile?.mkdir()
            FileOutputStream(storageFile).use { outputStream ->
                context.assets.open(assetsFilepath).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return storageFile.absolutePath
    }

}
