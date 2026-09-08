package com.ml.shubham0204.facenet_android.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest

class ModelUtils {
    companion object {
        // Resize the given bitmap and convert it to a ByteBuffer
        fun convertBitmapToBuffer(image: Bitmap, inputDim: Int, isRGB: Boolean = true): FloatArray {
            val resizedBitmap = image.scale(inputDim, inputDim, true)
            val pixels = IntArray(inputDim * inputDim)
            resizedBitmap.getPixels(pixels, 0, inputDim, 0, 0, inputDim, inputDim)
            val floats = FloatArray(inputDim * inputDim * 3)
            var i = 0
            val offsets = if (isRGB) {
                arrayOf(16, 8, 0)
            } else {
                arrayOf(0, 8, 16)
            }
            for (p in pixels) {
                floats[i++] = ((p shr offsets[0]) and 0xFF).toFloat() / 255f  // R
                floats[i++] = ((p shr offsets[1]) and 0xFF).toFloat() / 255f  // G
                floats[i++] = ((p shr offsets[2]) and 0xFF).toFloat() / 255f   // B
            }
            return floats
        }

        // Copy the file from the assets to the app's internal/private storage
        // and return its absolute path
        fun copyAndReturnPath(context: Context, assetsFilepath: String): String {
            val storageFile = File(context.filesDir, assetsFilepath)
            var shouldCopyFile = true
            if (storageFile.exists()) {
                val storageFileHash = FileInputStream(storageFile).use {
                    computeFileHash(it)
                }
                val assetsFileHash = context.assets.open(assetsFilepath).use {
                    computeFileHash(it)
                }
                shouldCopyFile = storageFileHash != assetsFileHash
            }
            if (shouldCopyFile) {
                storageFile.parentFile?.mkdir()
                FileOutputStream(storageFile).use { outputStream ->
                    context.assets.open(assetsFilepath).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            return storageFile.absolutePath
        }

        private fun computeFileHash(inputStream: InputStream, algorithm: String = "SHA-256"): String {
            val digest = MessageDigest.getInstance(algorithm)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
            return digest.digest().joinToString("") { "%02x".format(it) }
        }
    }
}