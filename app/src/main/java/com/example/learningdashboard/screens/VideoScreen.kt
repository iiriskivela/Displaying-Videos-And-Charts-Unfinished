package com.example.learningdashboard.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.CustomVideoPlayer
import com.example.learningdashboard.ViewModels.SharedViewModel

// --- Data class to hold video info ---
data class VideoItem(
    val id: String,
    val title: String,
    val uploader: String,
    val videoUrl: String
)

// Sample Video URLs
const val SAMPLE_VIDEO_URL_1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
const val SAMPLE_VIDEO_URL_2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
const val SAMPLE_VIDEO_URL_3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"

// --- Updated Sample Data ---
val sampleVideos = listOf(
    VideoItem("1", "Big Buck Bunny", "Blender Foundation", SAMPLE_VIDEO_URL_1),
    VideoItem("2", "Elephants Dream", "Blender Foundation", SAMPLE_VIDEO_URL_2),
    VideoItem("3", "Sintel", "Blender Foundation", SAMPLE_VIDEO_URL_3)
)

// --- Screen 1: Video ---
@Composable
fun VideoScreen(
    onNavigateToUpload: () -> Unit,
    onNavigateToFullScreen: (String) -> Unit, // --- New: Callback for fullscreen ---
    sharedViewModel: SharedViewModel
) {
    // Get the list of uploaded URIs
    val videoUris by sharedViewModel.videoUris.collectAsState()

    // --- Combine sample videos and uploaded videos into one list ---
    val allVideos = sampleVideos + videoUris.mapIndexed { index, uri ->
        VideoItem(
            id = "uploaded_$index",
            title = "Uploaded Video ${index + 1}",
            uploader = "You",
            videoUrl = uri.toString()
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToUpload) {
                Icon(Icons.Filled.Add, contentDescription = "Upload Video")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues
        ) {
            item {
                Text(
                    text = "Videos",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // TODO: [Exercise] Display the list of videos.
            // 1. Use the `items` function to iterate through the `allVideos` list.
            // 2. For each `video`, call the `VideoCardItem` composable.
            // 3. Pass the `video` and the `onNavigateToFullScreen` callback.
            // Start it this way:
            // items(allVideos, key = { it.id }) {TODO}

            item {
                // Add some space at the bottom
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --- New Video Card Composable ---
@Composable
fun VideoCardItem(
    video: VideoItem,
    onFullScreenClick: () -> Unit // --- New: Callback for click ---
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        // Note: We do NOT make the Card itself clickable,
        // so that the player controls inside it work.
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Video Player
            CustomVideoPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .background(Color.Black), // Background for the player
                videoUrl = video.videoUrl,
                onFullScreenClick = onFullScreenClick // --- New: Pass callback to player ---
            )

            // Video Title and Uploader Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Uploader Icon
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Uploader",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray, CircleShape)
                        .padding(4.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.size(12.dp))

                // Title and Uploader Text
                Column {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = video.uploader,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}