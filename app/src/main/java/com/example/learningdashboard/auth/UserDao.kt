package com.example.learningdashboard.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // Register: Insert a new user
    // OnConflictStrategy.ABORT: Throws an exception if the username (primary key) already exists
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Login: Find a user by username
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Register check: Find a user by Email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Removed updateUserName (which updated fullName)

    // Update username (primary key)
    @Query("UPDATE users SET username = :newUsername WHERE username = :oldUsername")
    suspend fun updateUsername(oldUsername: String, newUsername: String)

    // Update password: Update only passwordHash
    @Query("UPDATE users SET passwordHash = :passwordHash WHERE username = :username")
    suspend fun updatePassword(username: String, passwordHash: String)

    // --- New (for completeness, matches repository) ---
    // Update email: Update only email
    @Query("UPDATE users SET email = :email WHERE username = :username")
    suspend fun updateEmail(username: String, email: String)
}