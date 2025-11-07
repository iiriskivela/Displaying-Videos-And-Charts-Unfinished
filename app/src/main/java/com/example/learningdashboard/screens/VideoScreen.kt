package com.example.learningdashboard.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.CustomVideoPlayer
import com.example.learningdashboard.ViewModels.SharedViewModel

// Sample Video URLs
const val SAMPLE_VIDEO_URL_1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
const val SAMPLE_VIDEO_URL_2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
const val SAMPLE_VIDEO_URL_3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"

val sampleVideos = listOf(SAMPLE_VIDEO_URL_1, SAMPLE_VIDEO_URL_2, SAMPLE_VIDEO_URL_3)

// --- Screen 1: Video ---
@Composable
fun VideoScreen(
    onNavigateToUpload: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    val videoUris by sharedViewModel.videoUris.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToUpload) {
                Icon(Icons.Filled.Add, contentDescription = "Upload Video")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Course Videos",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(sampleVideos) { videoUrl ->
                    CustomVideoPlayer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        videoUrl = videoUrl
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 5.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Uploaded Videos",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(videoUris) { uri ->
                    CustomVideoPlayer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f),
                        videoUrl = uri.toString()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
