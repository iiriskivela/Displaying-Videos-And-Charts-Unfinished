package com.example.learningdashboard.ViewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedViewModel : ViewModel() {
    private val _videoUris = MutableStateFlow<List<Uri>>(emptyList())
    val videoUris: StateFlow<List<Uri>> = _videoUris.asStateFlow()

    fun addVideoUri(uri: Uri) {
        _videoUris.update { currentList -> currentList + uri }
    }
}
