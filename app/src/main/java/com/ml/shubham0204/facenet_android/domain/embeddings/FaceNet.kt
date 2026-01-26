package com.ml.shubham0204.facenet_android.domain.embeddings

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.TensorOperator
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.nio.ByteBuffer

// Derived from the original project:
// https://github.com/shubham0204/FaceRecognition_With_FaceNet_Android/blob/master/app/src/main/java/com/ml/quaterion/facenetdetection/model/FaceNetModel.kt
// Utility class for FaceNet model
@Single
class FaceNet(
    context: Context
) {
    // Input image size for FaceNet model.
    private val imgSize = 160

    // Output embedding size
    private val embeddingDim = 512

    private var compiledModel: CompiledModel
    private val inputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val outputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val imageTensorProcessor =
        ImageProcessor
            .Builder()
            .add(ResizeOp(imgSize, imgSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp())
            .build()

    init {
        compiledModel = CompiledModel.create(
            context.assets,
            "facenet_512.tflite",
            CompiledModel.Options(Accelerator.CPU)
        )
        inputBuffers = compiledModel.createInputBuffers()
        outputBuffers = compiledModel.createOutputBuffers()
    }

    // Gets an face embedding using FaceNet
    suspend fun getFaceEmbedding(image: Bitmap) =
        withContext(Dispatchers.Default) {
            return@withContext runFaceNet(convertBitmapToBuffer(image))[0]
        }

    // Run the FaceNet model
    private fun runFaceNet(inputs: ByteBuffer): Array<FloatArray> {
        inputBuffers[0].writeInt8(inputs.array())
        compiledModel.run(inputBuffers, outputBuffers)
        return arrayOf(outputBuffers[0].readFloat())
    }

    // Resize the given bitmap and convert it to a ByteBuffer
    private fun convertBitmapToBuffer(image: Bitmap): ByteBuffer =
        imageTensorProcessor.process(TensorImage.fromBitmap(image)).buffer

    class NormalizeOp : TensorOperator {
        override fun apply(p0: TensorBuffer?): TensorBuffer {
            val pixels = p0!!.floatArray.map { it / 255f }.toFloatArray()
            val output = TensorBufferFloat.createFixedSize(p0.shape, DataType.FLOAT32)
            output.loadArray(pixels)
            return output
        }
    }
}
