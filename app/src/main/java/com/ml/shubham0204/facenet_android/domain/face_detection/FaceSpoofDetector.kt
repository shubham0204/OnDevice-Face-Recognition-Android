package com.ml.shubham0204.facenet_android.domain.face_detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import kotlin.math.exp
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import androidx.core.graphics.scale
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel

/*

Utility class for interacting with FaceSpoofDetector

- It uses the MiniFASNet model from https://github.com/minivision-ai/Silent-Face-Anti-Spoofing
- The preprocessing methods are derived from
https://github.com/serengil/deepface/blob/master/deepface/models/spoofing/FasNet.py
- The model weights are in the PyTorch format. To convert them to the TFLite format,
  check the notebook linked in the README of the project
- An instance of this class is injected in ImageVectorUseCase.kt

*/
@Single
class FaceSpoofDetector(
    context: Context,
    useGpu: Boolean = false,
    useXNNPack: Boolean = false,
    useNNAPI: Boolean = false,
) {
    data class FaceSpoofResult(
        val isSpoof: Boolean,
        val score: Float,
        val timeMillis: Long,
    )

    private val scale1 = 2.7f
    private val scale2 = 4.0f
    private val inputImageDim = 80
    private val outputDim = 3

    private var compiledModel27: CompiledModel
    private var compiledModel40: CompiledModel
    private val compiledModel27InputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val compiledModel27OutputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val compiledModel40InputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val compiledModel40OutputBuffers: List<com.google.ai.edge.litert.TensorBuffer>
    private val imageTensorProcessor =
        ImageProcessor
            .Builder()
            .add(CastOp(DataType.FLOAT32))
            .build()

    init {
        compiledModel27 = CompiledModel.create(
            context.assets,
            "spoof_model_scale_2_7.tflite",
            CompiledModel.Options(Accelerator.GPU)
        )
        compiledModel27InputBuffers = compiledModel27.createInputBuffers()
        compiledModel27OutputBuffers = compiledModel27.createOutputBuffers()
        compiledModel40 = CompiledModel.create(
            context.assets,
            "spoof_model_scale_4_0.tflite",
            CompiledModel.Options(Accelerator.GPU)
        )
        compiledModel40InputBuffers = compiledModel40.createInputBuffers()
        compiledModel40OutputBuffers = compiledModel40.createOutputBuffers()
    }

    suspend fun detectSpoof(
        frameImage: Bitmap,
        faceRect: Rect,
    ): FaceSpoofResult =
        withContext(Dispatchers.Default) {
            // Crop the images and scale the bounding boxes
            // with the given two constants
            // and perform RGB -> BGR conversion
            val croppedImage1 =
                crop(
                    origImage = frameImage,
                    bbox = faceRect,
                    bboxScale = scale1,
                    targetWidth = inputImageDim,
                    targetHeight = inputImageDim,
                )
            for (i in 0 until croppedImage1.width) {
                for (j in 0 until croppedImage1.height) {
                    croppedImage1[i, j] =
                        Color.rgb(
                            Color.blue(croppedImage1[i, j]),
                            Color.green(croppedImage1[i, j]),
                            Color.red(croppedImage1[i, j]),
                        )
                }
            }
            val croppedImage2 =
                crop(
                    origImage = frameImage,
                    bbox = faceRect,
                    bboxScale = scale2,
                    targetWidth = inputImageDim,
                    targetHeight = inputImageDim,
                )
            for (i in 0 until croppedImage2.width) {
                for (j in 0 until croppedImage2.height) {
                    croppedImage2[i, j] =
                        Color.rgb(
                            Color.blue(croppedImage2[i, j]),
                            Color.green(croppedImage2[i, j]),
                            Color.red(croppedImage2[i, j]),
                        )
                }
            }
            val input1 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage1)).buffer
            val input2 = imageTensorProcessor.process(TensorImage.fromBitmap(croppedImage2)).buffer
            compiledModel27InputBuffers[0].writeInt8(input1.array())
            compiledModel40InputBuffers[0].writeInt8(input2.array())

            val time =
                measureTime {
                    compiledModel27.run(compiledModel27InputBuffers, compiledModel27OutputBuffers)
                    compiledModel40.run(compiledModel40InputBuffers, compiledModel40OutputBuffers)
                }.toLong(DurationUnit.MILLISECONDS)

            val output1 = arrayOf(compiledModel27OutputBuffers[0].readFloat())
            val output2 = arrayOf(compiledModel40OutputBuffers[0].readFloat())

            val output =
                softMax(output1[0]).zip(softMax(output2[0])).map {
                    (it.first + it.second)
                }
            val label = output.indexOf(output.max())
            val iSpoof = label != 1
            val score = output[label] / 2f

            return@withContext FaceSpoofResult(isSpoof = iSpoof, score = score, timeMillis = time)
        }

    private fun softMax(x: FloatArray): FloatArray {
        val exp = x.map { exp(it) }
        val expSum = exp.sum()
        return exp.map { it / expSum }.toFloatArray()
    }

    private fun crop(
        origImage: Bitmap,
        bbox: Rect,
        bboxScale: Float,
        targetWidth: Int,
        targetHeight: Int,
    ): Bitmap {
        val srcWidth = origImage.width
        val srcHeight = origImage.height
        val scaledBox = getScaledBox(srcWidth, srcHeight, bbox, bboxScale)
        val croppedBitmap =
            Bitmap.createBitmap(
                origImage,
                scaledBox.left,
                scaledBox.top,
                scaledBox.width(),
                scaledBox.height(),
            )
        return croppedBitmap.scale(targetWidth, targetHeight)
    }

    private fun getScaledBox(
        srcWidth: Int,
        srcHeight: Int,
        box: Rect,
        bboxScale: Float,
    ): Rect {
        val x = box.left
        val y = box.top
        val w = box.width()
        val h = box.height()
        val scale = floatArrayOf((srcHeight - 1f) / h, (srcWidth - 1f) / w, bboxScale).min()
        val newWidth = w * scale
        val newHeight = h * scale
        val centerX = w / 2 + x
        val centerY = h / 2 + y
        var topLeftX = centerX - newWidth / 2
        var topLeftY = centerY - newHeight / 2
        var bottomRightX = centerX + newWidth / 2
        var bottomRightY = centerY + newHeight / 2
        if (topLeftX < 0) {
            bottomRightX -= topLeftX
            topLeftX = 0f
        }
        if (topLeftY < 0) {
            bottomRightY -= topLeftY
            topLeftY = 0f
        }
        if (bottomRightX > srcWidth - 1) {
            topLeftX -= (bottomRightX - (srcWidth - 1))
            bottomRightX = (srcWidth - 1).toFloat()
        }
        if (bottomRightY > srcHeight - 1) {
            topLeftY -= (bottomRightY - (srcHeight - 1))
            bottomRightY = (srcHeight - 1).toFloat()
        }
        return Rect(topLeftX.toInt(), topLeftY.toInt(), bottomRightX.toInt(), bottomRightY.toInt())
    }
}
