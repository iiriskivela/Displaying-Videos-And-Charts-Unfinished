package com.example.learningdashboard // Make sure this is your package name

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun CustomVideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    onFullScreenClick: (() -> Unit)? = null // --- New: Optional callback ---
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Auto-play
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // Release player resources
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            // Create PlayerView
            PlayerView(context).apply {
                player = exoPlayer
                // --- New: Set listener if callback is provided ---
                onFullScreenClick?.let { onClick ->
                    setFullscreenButtonClickListener {
                        onClick()
                    }
                }
            }
        },
        // --- New: Update block to handle recomposition ---
        update = { playerView ->
            // Update the listener if the callback instance changes
            onFullScreenClick?.let { onClick ->
                playerView.setFullscreenButtonClickListener {
                    onClick()
                }
            } ?: playerView.setFullscreenButtonClickListener(null) // Clear listener
        }
    )
}