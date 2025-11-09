package com.example.learningdashboard.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.ViewModels.SharedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadVideoScreen(
    onNavigateToVideo: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    // --- The launcher updates the local state ---
    val pickVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedVideoUri = uri
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Select Video Button ---
            Button(
                onClick = { pickVideoLauncher.launch("video/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Video from Gallery")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedVideoUri != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Video:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedVideoUri.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TODO: [Exercise] Students need to implement the onClick for this button.
                // 1. Get the `selectedVideoUri` (make sure it's not null).
                // 2. Call the `addVideoUri` function on the `sharedViewModel`.
                // 3. Call the `onNavigateToVideo` function to go back to the previous screen.
                Button(
                    onClick = {
                        // TODO
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedVideoUri != null
                ) {
                    Text("Confirm Upload")
                }
            } else {
                Text("No video selected")
            }
        }
    }
}