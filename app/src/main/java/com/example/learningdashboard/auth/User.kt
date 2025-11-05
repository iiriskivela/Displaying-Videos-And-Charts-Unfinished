package com.example.learningdashboard.auth

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 这将是我们的数据库表 "users" 的结构
 *
 * @param username 主键，必须唯一
 * @param email 我们希望 email 也唯一，所以添加索引 (indices)
 * @param passwordHash !!重要!! 真实项目中你不应该存储明文密码。
 * 这里为了简单起见我们还是存储明文，但你应该存储哈希值。
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // 确保 Email 也是唯一的
)
data class User(
    @PrimaryKey
    val username: String,
    val email: String,
    val passwordHash: String, // 我们把 'password' 重命名为 'passwordHash' 以提醒自己
    val fullName: String,
    val registeredDate: Long
)