package com.example.learningdashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.auth.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 1. 从 ViewModel 收集完整的状态
    val authState by authViewModel.uiState.collectAsState()

    // 2. 本地的 errorMessage 状态已被移除

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 标题
        Text(
            text = "VidFlow", // 你选择的 App 名字
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                authViewModel.clearError() // 3. 用户开始输入时，清除 ViewModel 中的错误信息
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = authState.errorMessage != null // 4. 从 authState 获取错误状态
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.clearError() // 3. 用户开始输入时，清除 ViewModel 中的错误信息
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), // 隐藏密码
            isError = authState.errorMessage != null // 4. 从 authState 获取错误状态
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 5. 显示来自 ViewModel 的错误信息
        if (authState.errorMessage != null) {
            Text(
                text = authState.errorMessage!!,
                color = MaterialTheme.colorScheme.error, // 使用主题中的错误颜色
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // 6. 显示加载指示器或登录按钮
        if (authState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    // 7. onClick 只负责调用函数，不再接收返回值
                    authViewModel.login(username, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.isLoading // 加载时禁用按钮
            ) {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onRegisterClick,
            enabled = !authState.isLoading // 加载时禁用按钮
        ) {
            Text("Don't have an account? Register")
        }
    }
}