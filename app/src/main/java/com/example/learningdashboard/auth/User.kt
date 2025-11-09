package com.example.learningdashboard.auth

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * This will be the structure of our "users" database table
 *
 * @param username Primary key, must be unique
 * @param email We want email to be unique too, so add indices
 * @param passwordHash !!Important!! You should not store plaintext passwords in a real project.
 * For simplicity, we store plaintext here, but you should store a hash.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // Ensure Email is also unique
)
data class User(
    @PrimaryKey
    val username: String,
    val email: String,
    val passwordHash: String, // We rename 'password' to 'passwordHash' as a reminder
    val fullName: String,
    val registeredDate: Long
)