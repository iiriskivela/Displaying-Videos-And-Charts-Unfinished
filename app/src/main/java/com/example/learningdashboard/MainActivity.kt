package com.example.learningdashboard // Your package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.learningdashboard.auth.AuthViewModel
import com.example.learningdashboard.navigation.AuthNavigation
import com.example.learningdashboard.navigation.MainAppScreen
import com.example.learningdashboard.ui.theme.LearningDashboardTheme

// --- Activity ---
class MainActivity : ComponentActivity() {

    // 1. Get the AuthViewModel
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // --- Change is here ---
            // By setting dynamicColor to false,
            // we force the app to use the colors defined in Theme.kt and Color.kt,
            // rather than generating colors based on the user's wallpaper.
            LearningDashboardTheme(dynamicColor = false) {
                // 2. Observe the login state
                val authState by authViewModel.uiState.collectAsState()

                // 3. Determine which navigation flow to display based on login state
                if (authState.isLoggedIn) {
                    // If logged in, display the Main App
                    MainAppScreen(
                        authViewModel = authViewModel,
                        onLogout = { authViewModel.logout() }
                    )
                } else {
                    // If not logged in, display the authentication flow
                    AuthNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}