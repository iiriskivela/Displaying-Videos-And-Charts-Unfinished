package com.example.learningdashboard.auth

// No longer need SharedPreferences or Gson!

/**
 * The Repository now only depends on the DAO interface
 * It no longer holds any state (like that Map)
 */
class AuthRepository(private val userDao: UserDao) {

    // Simple pass-through functions
    // Error handling (like try-catch) will be the ViewModel's responsibility

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    /**
     * --- MODIFIED ---
     * This function now correctly maps to the new updateUsername function
     * in the UserDao.
     */
    suspend fun updateUsername(oldUsername: String, newUsername: String) {
        // This function now exists in UserDao.kt and updates the primary key
        userDao.updateUsername(oldUsername, newUsername)
    }

    // Pass-through for updating email
    suspend fun updateEmail(username: String, email: String) {
        userDao.updateEmail(username, email)
    }

    // Pass-through for updating password
    suspend fun updatePassword(username: String, passwordHash: String) {
        userDao.updatePassword(username, passwordHash)
    }

    // Note: loadUsers or saveUsers are no longer needed!
}