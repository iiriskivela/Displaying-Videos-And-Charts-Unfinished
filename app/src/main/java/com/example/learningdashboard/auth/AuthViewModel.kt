package com.example.learningdashboard.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Initialize database, DAO, and Repository
    private val repository: AuthRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = AuthRepository(userDao)
    }

    // 2. registeredUsers Map is gone! Data is now in the database.

    // Private mutable state
    private val _uiState = MutableStateFlow(AuthUiState())
    // Public immutable state flow
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Login.
     * No longer returns String?. It launches a coroutine and updates uiState.
     */
    fun login(user: String, pass: String) {
        // Start database operations in a coroutine
        viewModelScope.launch {
            // A. Start: Clear old errors and set loading state
            _uiState.update { it.copy(isLoading = true, profileErrorMessage = null) }

            // B. Validate input
            if (user.isBlank()) {
                _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Username cannot be empty") }
                return@launch
            }
            if (pass.isBlank()) {
                _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Password cannot be empty") }
                return@launch
            }

            // C. Access database
            try {
                // Note: We get data from the User.kt entity
                val userData = repository.getUserByUsername(user)

                if (userData == null) {
                    _uiState.update { it.copy(isLoading = false, profileErrorMessage = "User does not exist, please register first") }
                } else if (userData.passwordHash != pass) { // Check password (real app should check hash)
                    _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Incorrect password") }
                } else {
                    // D. Login success
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            username = userData.username,
                            email = userData.email,
                            fullName = userData.fullName,
                            registeredDate = userData.registeredDate
                        )
                    }
                }
            } catch (e: Exception) {
                // E. Handle database or other exceptions
                _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Error: ${e.message}") }
            }
        }
    }

    /**
     * Registration.
     * Also no longer returns String?.
     */
    fun register(user: String, email: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, profileErrorMessage = null) }

            // A. Validate all inputs (logic is same as before)
            val validationError = validateRegistration(user, email, pass, confirmPass)
            if (validationError != null) {
                _uiState.update { it.copy(isLoading = false, profileErrorMessage = validationError) }
                return@launch
            }

            // B. Check if user and Email already exist (database operations)
            try {
                if (repository.getUserByUsername(user) != null) {
                    _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Username is already taken") }
                    return@launch
                }
                if (repository.getUserByEmail(email) != null) {
                    _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Email is already registered") }
                    return@launch
                }

                // C. Registration success, create User object and save to database
                val currentTime = System.currentTimeMillis()
                val newUser = User( // Use User entity
                    username = user,
                    email = email,
                    passwordHash = pass, // Reminder: Should store a hash
                    fullName = user,
                    registeredDate = currentTime
                )

                repository.registerUser(newUser)

                // D. Auto-login
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        username = newUser.username,
                        email = newUser.email,
                        fullName = newUser.fullName,
                        registeredDate = newUser.registeredDate
                    )
                }

            } catch (e: Exception) {
                // E. Handle database insertion exceptions
                _uiState.update { it.copy(isLoading = false, profileErrorMessage = "Registration failed: ${e.message}") }
            }
        }
    }

    // Helper function: Extract validation logic
    private fun validateRegistration(user: String, email: String, pass: String, confirmPass: String): String? {
        if (user.isBlank()) return "Username cannot be empty"
        if (user.length < 3) return "Username must be at least 3 characters"
        if (!user.matches(Regex("^[a-zA-Z0-9_]+$"))) return "Username can only contain letters, numbers and underscores"
        if (email.isBlank()) return "Email cannot be empty"
        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) return "Invalid email format"
        if (pass.isBlank()) return "Password cannot be empty"
        if (pass.length < 6) return "Password must be at least 6 characters"
        if (pass != confirmPass) return "Passwords do not match"
        return null // All checks passed
    }



    /**
     * --- Updated ---
     * Update user password (using more robust validation from ProfileViewModel)
     */
    fun updatePassword(currentPass: String, newPass: String, confirmPass: String) {
        val currentUser = _uiState.value.username ?: return // Must be logged in

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPasswordLoading = true,
                    passwordErrorMessage = null,
                    passwordUpdateSuccess = false // <-- Reset on new attempt
                )
            }

            // Validation from ProfileViewModel
            if (currentPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
                _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "All fields are required") }
                return@launch
            }
            if (newPass != confirmPass) {
                _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "New passwords do not match") }
                return@launch
            }
            if (newPass.length < 6) { // Assuming a 6-character minimum
                _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "Password must be at least 6 characters") }
                return@launch
            }
            if (newPass == currentPass) {
                _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "New password cannot be the same as the old one") }
                return@launch
            }

            try {
                // Check if current password is correct
                val userData = repository.getUserByUsername(currentUser)
                if (userData == null) {
                    _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "User not found") }
                    return@launch
                }

                if (userData.passwordHash != currentPass) { // Simple check, real app should compare hash
                    _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "Incorrect current password") }
                    return@launch
                }

                // All checks passed, update password
                repository.updatePassword(currentUser, newPass) // Real app should pass a hash of newPass
                _uiState.update {
                    it.copy(
                        isPasswordLoading = false,
                        passwordErrorMessage = null,
                        passwordUpdateSuccess = true // <-- *** SET TO TRUE ***
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isPasswordLoading = false, passwordErrorMessage = "Update failed: ${e.message}") }
            }
        }
    }


    /**
     * Logout
     */
    fun logout() {
        // Reset to initial state on logout
        _uiState.value = AuthUiState()
    }

    /**
     * Helper: Allows UI to clear profile error when user starts typing
     */
    fun clearProfileError() {
        _uiState.update { it.copy(profileErrorMessage = null) }
    }

    /**
     * --- New ---
     * Helper: Allows UI to clear password error when user starts typing
     */
    fun clearPasswordError() {
        _uiState.update { it.copy(passwordErrorMessage = null, passwordUpdateSuccess = false) } // <-- *** MODIFIED ***
    }
    fun updateUsername(newUsername: String) {
        // (确保 newUsername 不为空，并且可能需要添加更多验证)
        if (newUsername.isBlank()) {
            _uiState.value = _uiState.value.copy(
                profileErrorMessage = "Username cannot be empty."
            )
            return
        }

        // (你可能还需要检查新用户名是否已被占用)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isProfileLoading = true)

                val oldUsername = _uiState.value.username
                if (oldUsername == null) {
                    // 如果旧用户名为空，则无法更新
                    _uiState.value = _uiState.value.copy(
                        profileErrorMessage = "Cannot update username: user not found.",
                        isProfileLoading = false
                    )
                    return@launch
                }

                // 1. 调用 Repository 更新数据库
                repository.updateUsername(oldUsername, newUsername)

                // 2. 更新 UI 状态
                _uiState.value = _uiState.value.copy(
                    username = newUsername,
                    isProfileLoading = false,
                    profileErrorMessage = null // 清除错误
                )

            } catch (e: Exception) {
                // (处理可能的错误，例如新用户名已存在)
                _uiState.value = _uiState.value.copy(
                    profileErrorMessage = "Error updating username: ${e.message}",
                    isProfileLoading = false
                )
            }
        }
    }
}


/**
 * UI state - Updated Version
 *
 * Removed UserData, which is now User.kt (database entity)
 * Added isLoading and errorMessage to drive UI states
 * ---
 * Renamed isLoading/errorMessage to be specific for profile/password
 * to prevent UI conflicts.
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val registeredDate: Long? = null,

    // General loading for Login/Register
    val isLoading: Boolean = false,

    // States for "Update Full Name"
    val isProfileLoading: Boolean = false,
    val profileErrorMessage: String? = null,

    // States for "Update Password"
    val isPasswordLoading: Boolean = false,
    val passwordErrorMessage: String? = null,
    val passwordUpdateSuccess: Boolean = false // <-- *** ADDED ***
)