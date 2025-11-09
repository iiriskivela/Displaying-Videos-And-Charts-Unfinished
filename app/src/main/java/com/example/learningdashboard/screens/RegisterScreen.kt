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


    val uiState by authViewModel.uiState.collectAsState()


    val isLoading = uiState.isLoading
    val errorMessage = uiState.profileErrorMessage



    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Create account", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = user,

            onValueChange = {
                user = it
                authViewModel.clearProfileError()
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.clearProfileError()
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                authViewModel.clearProfileError()
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
                authViewModel.clearProfileError()
            },
            label = { Text("Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Button(
            onClick = {

                authViewModel.register(
                    user = user.trim(),
                    email = email.trim(),
                    pass = pass,
                    confirmPass = confirm
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) { Text("Register") }

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) { Text("Have an account? Sign in") }

        if (errorMessage != null) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}