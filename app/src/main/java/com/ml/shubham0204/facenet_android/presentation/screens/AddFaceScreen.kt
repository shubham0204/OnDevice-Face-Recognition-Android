package com.ml.shubham0204.facenet_android.presentation.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ml.shubham0204.facenet_android.presentation.theme.FaceNetAndroidTheme
import com.ml.shubham0204.facenet_android.presentation.viewmodels.AddFaceScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AddFaceScreen(
    onNavigateBack: (() -> Unit)
) {
    FaceNetAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Add Faces", style = MaterialTheme.typography.headlineSmall)
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Navigate Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                val viewModel: AddFaceScreenViewModel = hiltViewModel()
                ScreenUI(viewModel)
            }
        }
    }
}

@Composable
private fun ScreenUI(viewModel: AddFaceScreenViewModel) {
    val pickVisualMediaLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia()
        ) {
            viewModel.selectedImageURIs.value = it
        }
    var personName by remember { viewModel.personNameState }
    TextField(
        value = personName,
        onValueChange = { personName = it },
        placeholder = { Text(text = "Enter the person's name") },
        singleLine = true
    )
    Row {
        Button(
            enabled = viewModel.personNameState.value.isNotEmpty(),
            onClick = {
                pickVisualMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add photos")
            Text(text = "Add photos")
        }
        if (viewModel.selectedImageURIs.value.isNotEmpty()) {
            Button(onClick = { viewModel.addImages() }) {
                Text(text = "Read images")
            }
        }
    }

    Text(
        text = "${viewModel.selectedImageURIs.value.size} image(s) selected",
        style = MaterialTheme.typography.labelSmall
    )
    ImagesGrid(viewModel)
}

@Composable
private fun ImagesGrid(viewModel: AddFaceScreenViewModel) {
    val uris by remember { viewModel.selectedImageURIs }
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(uris) { AsyncImage(model = it, contentDescription = null) }
    }
}
