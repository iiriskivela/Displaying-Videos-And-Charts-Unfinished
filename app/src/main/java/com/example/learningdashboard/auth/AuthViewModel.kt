package com.example.learningdashboard.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    // User data storage (simulating database)
    private val registeredUsers = mutableMapOf<String, UserData>()

    // Private mutable state
    private val _uiState = MutableStateFlow(AuthUiState())
    // Public immutable state flow
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Login validation
     * @return error message, null means success
     */
    fun login(user: String, pass: String): String? {
        // Validate input
        if (user.isBlank()) {
            return "Username cannot be empty"
        }
        if (pass.isBlank()) {
            return "Password cannot be empty"
        }

        // Check if user exists
        val userData = registeredUsers[user]
        if (userData == null) {
            return "User does not exist, please register first"
        }

        // Verify password
        if (userData.password != pass) {
            return "Incorrect password"
        }

        // Login successful
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                username = user,
                email = userData.email,
                fullName = userData.fullName,
                registeredDate = userData.registeredDate
            )
        }
        return null
    }

    /**
     * Registration validation
     * @return error message, null means success
     */
    fun register(user: String, email: String, pass: String, confirmPass: String): String? {
        // Validate username
        if (user.isBlank()) {
            return "Username cannot be empty"
        }
        if (user.length < 3) {
            return "Username must be at least 3 characters"
        }
        if (!user.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return "Username can only contain letters, numbers and underscores"
        }

        // Validate email
        if (email.isBlank()) {
            return "Email cannot be empty"
        }
        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return "Invalid email format"
        }

        // Validate password
        if (pass.isBlank()) {
            return "Password cannot be empty"
        }
        if (pass.length < 6) {
            return "Password must be at least 6 characters"
        }
        if (pass != confirmPass) {
            return "Passwords do not match"
        }

        // Check if username already exists
        if (registeredUsers.containsKey(user)) {
            return "Username is already taken"
        }

        // Check if email is already registered
        if (registeredUsers.values.any { it.email == email }) {
            return "Email is already registered"
        }

        // Registration successful, save user data
        val currentTime = System.currentTimeMillis()
        registeredUsers[user] = UserData(
            username = user,
            email = email,
            password = pass,
            fullName = user,
            registeredDate = currentTime
        )

        // Auto login
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                username = user,
                email = email,
                fullName = user,
                registeredDate = currentTime
            )
        }
        return null
    }

    /**
     * Update user profile
     */
    fun updateProfile(fullName: String): String? {
        val currentUser = _uiState.value.username ?: return "Not logged in"

        if (fullName.isBlank()) {
            return "Name cannot be empty"
        }

        // Update stored user data
        registeredUsers[currentUser]?.let { userData ->
            registeredUsers[currentUser] = userData.copy(fullName = fullName)
        }

        // Update UI state
        _uiState.update {
            it.copy(fullName = fullName)
        }
        return null
    }

    /**
     * Logout
     */
    fun logout() {
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                username = null,
                email = null,
                fullName = null,
                registeredDate = null
            )
        }
    }
}

/**
 * Stored user data
 */
data class UserData(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val registeredDate: Long
)

/**
 * UI state
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val registeredDate: Long? = null
)