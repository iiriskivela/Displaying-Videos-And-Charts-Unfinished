package com.example.learningdashboard.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // 注册：插入一个新用户
    // OnConflictStrategy.ABORT：如果用户名（主键）已存在，则抛出异常
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // 登录：根据用户名查找用户
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // 注册检查：根据 Email 查找用户
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // 更新个人资料：仅更新 fullName
    @Query("UPDATE users SET fullName = :fullName WHERE username = :username")
    suspend fun updateFullName(username: String, fullName: String)
}