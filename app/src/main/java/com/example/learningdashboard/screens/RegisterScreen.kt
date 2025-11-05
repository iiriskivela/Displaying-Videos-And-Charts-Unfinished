package com.example.learningdashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
// 导入 collectAsState
import androidx.compose.runtime.collectAsState
import com.example.learningdashboard.auth.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit
) {
    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // ✅ 1. 订阅 ViewModel 的 UI State
    val uiState by authViewModel.uiState.collectAsState()

    // ✅ 2. 从 State 中获取 isLoading 和 errorMessage
    val isLoading = uiState.isLoading
    val errorMessage = uiState.errorMessage

    // ❌ 3. 移除本地的 error 状态
    // var error by remember { mutableStateOf<String?>(null) } // <-- 已删除

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Create account", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = user,
            // ✅ 推荐：当用户输入时清除错误
            onValueChange = {
                user = it
                authViewModel.clearError()
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // ✅ 推荐：加载时禁用
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.clearError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                authViewModel.clearError()
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        OutlinedTextField(
            value = confirm,
            onValueChange = {
                confirm = it
                authViewModel.clearError()
            },
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Button(
            onClick = {
                // ✅ 4. 只调用函数，不接收返回值
                authViewModel.register(
                    user = user.trim(),
                    email = email.trim(),
                    pass = pass,
                    confirmPass = confirm
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // ✅ 推荐：加载时禁用
        ) { Text("Register") }

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) { Text("Have an account? Sign in") }

        // ✅ 5. 显示来自 ViewModel state 的错误
        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}