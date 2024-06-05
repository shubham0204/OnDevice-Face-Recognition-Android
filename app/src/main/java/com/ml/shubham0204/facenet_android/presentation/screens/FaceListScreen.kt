package com.ml.shubham0204.facenet_android.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ml.shubham0204.facenet_android.presentation.theme.FaceNetAndroidTheme

@Composable
fun FaceListScreen() {
    FaceNetAndroidTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) { ScreenUI() }
        }
    }
}

@Composable
private fun ScreenUI() {
    Text(text = "AddFaceScreen")
}
