package com.ml.shubham0204.facenet_android.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ml.shubham0204.facenet_android.data.PersonRecord
import com.ml.shubham0204.facenet_android.presentation.theme.FaceNetAndroidTheme
import com.ml.shubham0204.facenet_android.presentation.viewmodels.FaceListScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceListScreen(
    onNavigateBack: (() -> Unit) ,
    onAddFaceClick: (() -> Unit)
) {
    FaceNetAndroidTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Face List", style = MaterialTheme.typography.headlineSmall)
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Navigate Back"
                            )
                        }
                    },
                )
            } ,
            floatingActionButton = {
                FloatingActionButton(onClick = onAddFaceClick ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add a new face")
                }
            }
        ) { innerPadding ->
            val viewModel: FaceListScreenViewModel = hiltViewModel()
            Column(modifier = Modifier.padding(innerPadding)) { ScreenUI(viewModel) }
        }
    }
}

@Composable
private fun ScreenUI(viewModel: FaceListScreenViewModel) {
    val faces by viewModel.personFlow.collectAsState(emptyList())
    LazyColumn {
        items( faces ) {
            FaceListItem(it)
        }
    }
}

@Composable
private fun FaceListItem(personRecord: PersonRecord) {
    Column {
        Text(text = personRecord.personName)
        Text(text = personRecord.numImages.toString())
    }
}
