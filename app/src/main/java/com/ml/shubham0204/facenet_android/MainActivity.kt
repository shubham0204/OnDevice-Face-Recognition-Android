package com.ml.shubham0204.facenet_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ml.shubham0204.facenet_android.presentation.screens.AddFaceScreen
import com.ml.shubham0204.facenet_android.presentation.screens.DetectScreen
import com.ml.shubham0204.facenet_android.presentation.screens.FaceListScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = "chat",
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) {
                composable("add-face") {
                    AddFaceScreen()
                }
                composable("detect") {
                    DetectScreen()
                }
                composable("face-list"){
                    FaceListScreen()
                }
            }
        }
    }

}