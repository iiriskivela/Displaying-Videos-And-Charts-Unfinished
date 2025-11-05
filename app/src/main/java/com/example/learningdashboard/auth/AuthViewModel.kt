package com.example.learningdashboard.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // 1. 初始化数据库、DAO 和 Repository
    private val repository: AuthRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = AuthRepository(userDao)
    }

    // 2. registeredUsers Map 已被删除！数据现在在数据库中。

    // Private mutable state
    private val _uiState = MutableStateFlow(AuthUiState())
    // Public immutable state flow
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Login.
     * 不再返回 String?。它会启动一个协程并更新 uiState。
     */
    fun login(user: String, pass: String) {
        // 在协程中启动数据库操作
        viewModelScope.launch {
            // A. 开始：清除旧错误并设置加载状态
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // B. 验证输入
            if (user.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Username cannot be empty") }
                return@launch
            }
            if (pass.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Password cannot be empty") }
                return@launch
            }

            // C. 访问数据库
            try {
                // 注意：我们从 User.kt 实体获取数据
                val userData = repository.getUserByUsername(user)

                if (userData == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "User does not exist, please register first") }
                } else if (userData.passwordHash != pass) { // 检查密码 (真实项目应检查哈希值)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Incorrect password") }
                } else {
                    // D. 登录成功
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
                // E. 处理数据库或其他异常
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    /**
     * Registration.
     * 同样，不再返回 String?。
     */
    fun register(user: String, email: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // A. 验证所有输入 (这部分逻辑和以前一样)
            val validationError = validateRegistration(user, email, pass, confirmPass)
            if (validationError != null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = validationError) }
                return@launch
            }

            // B. 检查用户和 Email 是否已存在 (数据库操作)
            try {
                if (repository.getUserByUsername(user) != null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Username is already taken") }
                    return@launch
                }
                if (repository.getUserByEmail(email) != null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Email is already registered") }
                    return@launch
                }

                // C. 注册成功, 创建 User 对象并存入数据库
                val currentTime = System.currentTimeMillis()
                val newUser = User( // 使用 User 实体
                    username = user,
                    email = email,
                    passwordHash = pass, // 再次提醒：应该存储哈希值
                    fullName = user,
                    registeredDate = currentTime
                )

                repository.registerUser(newUser)

                // D. 自动登录
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
                // E. 处理数据库插入异常
                _uiState.update { it.copy(isLoading = false, errorMessage = "Registration failed: ${e.message}") }
            }
        }
    }

    // 辅助函数：将验证逻辑提取出来
    private fun validateRegistration(user: String, email: String, pass: String, confirmPass: String): String? {
        if (user.isBlank()) return "Username cannot be empty"
        if (user.length < 3) return "Username must be at least 3 characters"
        if (!user.matches(Regex("^[a-zA-Z0-9_]+$"))) return "Username can only contain letters, numbers and underscores"
        if (email.isBlank()) return "Email cannot be empty"
        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) return "Invalid email format"
        if (pass.isBlank()) return "Password cannot be empty"
        if (pass.length < 6) return "Password must be at least 6 characters"
        if (pass != confirmPass) return "Passwords do not match"
        return null // 全部通过
    }

    /**
     * Update user profile
     */
    fun updateProfile(fullName: String) {
        val currentUser = _uiState.value.username ?: return // 必须登录

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            if (fullName.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Name cannot be empty") }
                return@launch
            }

            try {
                repository.updateFullName(currentUser, fullName)
                // 更新 UI state
                _uiState.update {
                    it.copy(isLoading = false, fullName = fullName)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Update failed: ${e.message}") }
            }
        }
    }

    /**
     * Logout
     */
    fun logout() {
        // 登出时重置为初始状态
        _uiState.value = AuthUiState()
    }

    /**
     * 辅助函数：允许 UI 在用户开始输入时清除错误信息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


/**
 * UI state - 这是更新后的版本
 *
 * 移除了 UserData，因为它现在是 User.kt (数据库实体)
 * 添加了 isLoading 和 errorMessage 来驱动 UI 状态
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val registeredDate: Long? = null,

    // 新增状态，用于驱动 UI
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)