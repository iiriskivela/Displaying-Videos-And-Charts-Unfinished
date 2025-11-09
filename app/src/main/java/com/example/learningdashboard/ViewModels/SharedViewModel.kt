package com.example.learningdashboard.ViewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedViewModel : ViewModel() {
    // TODO: [Exercise] Students need to implement the state management for video URIs.
    // 1. Create a private MutableStateFlow to hold a list of video URIs.
    //    It should be initialized with an empty list.
    //    Start it this way:
    //    private val _videoUris = TODO

    // 2. Expose the private state flow as a public, read-only StateFlow.
    //    Start it this way_
    //    val videoUris: TODO

    // 3. Create a function to add a new video URI to the list.
    //    This function should update the state flow.
    //    Start it this way:
    //    fun addVideoUri(uri: Uri) {TODO}

}
