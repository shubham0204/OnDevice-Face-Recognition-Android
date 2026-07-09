package com.ml.shubham0204.facenet_android

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ml.shubham0204.facenet_android.domain.NativeFaceRecognitionModule
import com.ml.shubham0204.facenet_android.presentation.screens.add_face.AddFaceScreen
import com.ml.shubham0204.facenet_android.presentation.screens.detect_screen.DetectScreen
import com.ml.shubham0204.facenet_android.presentation.screens.face_list.FaceListScreen
import okhttp3.internal.format
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = "detect",
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
            ) {
                composable("add-face") { AddFaceScreen { navHostController.navigateUp() } }
                composable("detect") { DetectScreen { navHostController.navigate("face-list") } }
                composable("face-list") {
                    FaceListScreen(
                        onNavigateBack = { navHostController.navigateUp() },
                        onAddFaceClick = { navHostController.navigate("add-face") },
                    )
                }
            }
        }

//        val nativeFaceRecognitionModule = NativeFaceRecognitionModule()
//        nativeFaceRecognitionModule.createFaceRecognizer(
//            File(filesDir, "vectordb.bin").absolutePath,
//            "/data/local/tmp/facenet.pte"
//        )
//
//        val img = BitmapFactory.decodeStream(assets.open("img.png"))
//        val duration1 = measureTime { nativeFaceRecognitionModule.insert(
//            "Shubham",
//            listOf(img)
//        ) }
//        val result = measureTimedValue { nativeFaceRecognitionModule.recognize(img) }
//        val t1 = result.duration.toLong(DurationUnit.MILLISECONDS)
//        val t2 = duration1.toLong(DurationUnit.MILLISECONDS)
//        println("$t1 $t2")
    }
}
