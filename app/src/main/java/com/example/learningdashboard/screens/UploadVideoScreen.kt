package com.example.learningdashboard.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.ViewModels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadVideoScreen(
    onNavigateToVideo: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            videoUri = uri
            uri?.let { sharedViewModel.addVideoUri(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload a New Video") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToVideo) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Button(
                onClick = { pickVideoLauncher.launch("video/*") }
            ) {
                Text("Select Video")
            }
            videoUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selected video: $it")
            }
        }
    }
}