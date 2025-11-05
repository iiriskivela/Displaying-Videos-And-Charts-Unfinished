package com.example.learningdashboard.auth

// 不再需要 SharedPreferences 或 Gson 了！

/**
 * Repository 现在只依赖 DAO 接口
 * 它不再持有任何状态 (比如那个 Map)
 */
class AuthRepository(private val userDao: UserDao) {

    // 简单的 "直通" (pass-through) 函数
    // 错误处理（如 try-catch）将由 ViewModel 负责

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateFullName(username: String, fullName: String) {
        userDao.updateFullName(username, fullName)
    }

    // 注意：不再需要 loadUsers 或 saveUsers 了！
}