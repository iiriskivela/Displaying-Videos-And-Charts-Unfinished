package com.example.learningdashboard.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // In a real app, you would fetch this from a repository
    fun fetchUserData() {
        viewModelScope.launch {
            // Dummy data for now
            _user.value = User(
                username = "johndoe",
                email = "john.doe@example.com",
                passwordHash = "", // Should not be exposed to UI
                fullName = "John Doe",
                registeredDate = System.currentTimeMillis()
            )
        }
    }
}