package com.example.learningdashboard.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
// import androidx.lifecycle.viewmodel.compose.viewModel // 1. No longer needed
// import com.example.learningdashboard.ViewModels.ProfileViewModel // 2. No longer needed
// import com.example.learningdashboard.ViewModels.ProfileUiState // 3. No longer needed
import com.example.learningdashboard.auth.AuthUiState
import com.example.learningdashboard.auth.AuthViewModel // <-- *** IMPORT AuthViewModel ***
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    // We still need AuthUiState to display username, email, etc.
    authState: AuthUiState,
    // We no longer need all the separate lambdas,
    // we'll get the AuthViewModel from the NavGraph
    authViewModel: AuthViewModel? // 4. <-- *** MODIFIED *** (Nullable for preview)
) {
    // 1. State for the USERNAME input
    var usernameInput by remember { mutableStateOf("") } // <-- *** MODIFIED ***

    // --- States for Password Update ---
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // --- State for Tabs ---
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("Edit Username", "Change Password") // <-- *** MODIFIED ***

    // 5. We NO LONGER observe ProfileViewModel. We use authState directly.

    // 2. Sync USERNAME input when authState changes
    LaunchedEffect(authState.username) { // <-- *** MODIFIED ***
        usernameInput = authState.username ?: "" // <-- *** MODIFIED ***
    }

    // 3. Clear profile error when user starts typing USERNAME
    LaunchedEffect(usernameInput) { // <-- *** MODIFIED ***
        if (authState.profileErrorMessage != null) {
            authViewModel?.clearProfileError() // <-- *** MODIFIED ***
        }
    }

    // --- Clear password error OR success when user starts typing passwords ---
    LaunchedEffect(currentPassword, newPassword, confirmPassword) {
        if (authState.passwordErrorMessage != null || authState.passwordUpdateSuccess) { // <-- *** MODIFIED ***
            authViewModel?.clearPasswordError() // <-- *** MODIFIED ***
        }
    }

    // --- NEW: Detect password change success and clear fields ---
    LaunchedEffect(authState.passwordUpdateSuccess) { // <-- *** MODIFIED ***
        if (authState.passwordUpdateSuccess) { // <-- *** MODIFIED ***
            // Clear fields only on success
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Page",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        // 4. Display read-only user information (from AuthState)
        // This is now always up-to-date because AuthViewModel's state is updated
        UserInfoCard(
            username = authState.username,
            email = authState.email,
            registeredDate = authState.registeredDate
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. TabRow container
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        // --- NEW: Clear states when switching tabs ---
                        authViewModel?.clearPasswordError() // <-- *** MODIFIED ***
                        authViewModel?.clearProfileError() // <-- *** MODIFIED ***
                    },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Conditional content based on selected tab
        when (selectedTabIndex) {
            // Tab 0: Edit Name
            0 -> {
                EditProfileCard(
                    usernameInput = usernameInput, // <-- *** MODIFIED ***
                    onUsernameChange = { usernameInput = it }, // <-- *** MODIFIED ***
                    // 6. Pass the full AuthUiState
                    authState = authState, // <-- *** MODIFIED ***
                    // 7. Call the AuthViewModel function directly
                    onUpdateProfile = {
                        // We use the function that updates both DB and UI state
                        // *** 重要提示: 你需要在 AuthViewModel 中实现 updateUsername 方法 ***
                        authViewModel?.updateUsername(usernameInput) // <-- *** MODIFIED ***
                    }
                )
            }

            // Tab 1: Change Password
            1 -> {
                ChangePasswordCard(
                    currentPassword = currentPassword,
                    onCurrentPasswordChange = { currentPassword = it },
                    currentPasswordVisible = currentPasswordVisible,
                    onCurrentPasswordVisibilityChange = { currentPasswordVisible = !currentPasswordVisible },
                    newPassword = newPassword,
                    onNewPasswordChange = { newPassword = it },
                    newPasswordVisible = newPasswordVisible,
                    onNewPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    confirmPasswordVisible = confirmPasswordVisible,
                    onConfirmPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                    // 8. Pass the full AuthUiState
                    authState = authState, // <-- *** MODIFIED ***
                    // 9. Call the AuthViewModel function directly
                    onUpdatePassword = {
                        authViewModel?.updatePassword( // <-- *** MODIFIED ***
                            currentPassword,
                            newPassword,
                            confirmPassword
                        )
                    }
                )
            }
        }
    }
}

// ===================================================================
// UserInfoCard, ProfileInfoRow, PasswordTextField,
// and toFormattedDate REMAIN THE SAME
// ...
// The signatures for EditProfileCard and ChangePasswordCard are modified
// ===================================================================

@Composable
private fun EditProfileCard(
    usernameInput: String, // <-- *** MODIFIED ***
    onUsernameChange: (String) -> Unit, // <-- *** MODIFIED ***
    authState: AuthUiState, // <-- *** MODIFIED ***
    onUpdateProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Edit Your Username", // <-- *** MODIFIED ***
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = usernameInput, // <-- *** MODIFIED ***
                onValueChange = onUsernameChange, // <-- *** MODIFIED ***
                label = { Text("Username") }, // <-- *** MODIFIED ***
                modifier = Modifier.fillMaxWidth(),
                // 10. Use the AuthUiState
                isError = authState.profileErrorMessage != null, // <-- *** MODIFIED ***
                singleLine = true
            )

            // 11. Use the AuthUiState
            if (authState.profileErrorMessage != null) { // <-- *** MODIFIED ***
                Text(
                    text = authState.profileErrorMessage, // <-- *** MODIFIED ***
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUpdateProfile,
                // 12. Use the AuthUiState
                enabled = !authState.isProfileLoading, // <-- *** MODIFIED ***
                modifier = Modifier.fillMaxWidth()
            ) {
                // 13. Use the AuthUiState
                if (authState.isProfileLoading) { // <-- *** MODIFIED ***
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}

@Composable
private fun ChangePasswordCard(
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    currentPasswordVisible: Boolean,
    onCurrentPasswordVisibilityChange: () -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    newPasswordVisible: Boolean,
    onNewPasswordVisibilityChange: () -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    authState: AuthUiState, // <-- *** MODIFIED ***
    onUpdatePassword: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Current Password
            PasswordTextField(
                value = currentPassword,
                onValueChange = onCurrentPasswordChange,
                label = "Current Password",
                isVisible = currentPasswordVisible,
                onVisibilityChange = onCurrentPasswordVisibilityChange,
                // 14. Error check is now based on the error message
                isError = authState.passwordErrorMessage != null // <-- *** MODIFIED ***
            )
            Spacer(modifier = Modifier.height(8.dp))

            // New Password
            PasswordTextField(
                value = newPassword,
                onValueChange = onNewPasswordChange,
                label = "New Password",
                isVisible = newPasswordVisible,
                onVisibilityChange = onNewPasswordVisibilityChange,
                isError = authState.passwordErrorMessage != null // <-- *** MODIFIED ***
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Confirm New Password
            PasswordTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm New Password",
                isVisible = confirmPasswordVisible,
                onVisibilityChange = onConfirmPasswordVisibilityChange,
                isError = authState.passwordErrorMessage != null // <-- *** MODIFIED ***
            )

            // 15. Use the AuthUiState
            if (authState.passwordErrorMessage != null) { // <-- *** MODIFIED ***
                Text(
                    text = authState.passwordErrorMessage, // <-- *** MODIFIED ***
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            // 16. Use the AuthUiState
            if (authState.passwordUpdateSuccess) { // <-- *** MODIFIED ***
                Text(
                    text = "Password updated successfully!",
                    color = MaterialTheme.colorScheme.primary, // Non-error color
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUpdatePassword,
                // 17. Use the AuthUiState
                enabled = !authState.isPasswordLoading, // <-- *** MODIFIED ***
                modifier = Modifier.fillMaxWidth()
            ) {
                // 18. Use the AuthUiState
                if (authState.isPasswordLoading) { // <-- *** MODIFIED ***
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Update Password")
                }
            }
        }
    }
}


@Composable
private fun UserInfoCard(
    username: String?,
    email: String?,
    registeredDate: Long?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileInfoRow(label = "Username", value = username ?: "N/A")
            ProfileInfoRow(label = "Email", value = email ?: "N/A")
            ProfileInfoRow(
                label = "Member Since",
                value = registeredDate?.toFormattedDate() ?: "N/A"
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(0.4f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (isVisible)
                Icons.Filled.Visibility
            else
                Icons.Filled.VisibilityOff

            val description = if (isVisible) "Hide password" else "Show password"

            IconButton(onClick = onVisibilityChange) {
                Icon(imageVector = image, description)
            }
        }
    )
}


// Helper function (no changes)
private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yyyY", Locale.getDefault())
    return format.format(date)
}