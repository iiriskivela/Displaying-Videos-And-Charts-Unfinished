@file:OptIn(ExperimentalMaterial3Api::class) // <-- Corrected: Removed the 'D'

package com.example.learningdashboard.navigation

// import kotlin.OptIn // <-- Removed this, it's not needed
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// import com.example.learningdashboard.ViewModels.ProfileViewModel // Not needed
import com.example.learningdashboard.ViewModels.SharedViewModel
import com.example.learningdashboard.auth.AuthUiState // Import AuthUiState
import com.example.learningdashboard.auth.AuthViewModel
import com.example.learningdashboard.screens.FullScreenVideoScreen
import com.example.learningdashboard.screens.ProfileScreen
import com.example.learningdashboard.screens.ProgressScreen
import com.example.learningdashboard.screens.UploadVideoScreen
import com.example.learningdashboard.screens.VideoScreen
import com.example.learningdashboard.ui.theme.LearningDashboardTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Navigation route definitions ---
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Video    : Screen("video",    "Videos",     Icons.Default.PlayArrow)
    data object Progress : Screen("progress", "Usage Status", Icons.Default.Star)
    data object Profile  : Screen("profile",  "Profile",          Icons.Default.Person)
}

private val items = listOf(
    Screen.Video,
    Screen.Progress,
    Screen.Profile
)

// --- Constants for Fullscreen Video Route ---
private const val VIDEO_URL_ARG = "videoUrl"
private const val FULLSCREEN_VIDEO_ROUTE_TEMPLATE = "fullScreenVideo/{$VIDEO_URL_ARG}"

/**
 * Creates a navigation route for the fullscreen video screen,
 * ensuring the video URL is properly URL-encoded.
 */
private fun createFullScreenVideoRoute(videoUrl: String): String {
    val encodedUrl = URLEncoder.encode(videoUrl, StandardCharsets.UTF_8.toString())
    return "fullScreenVideo/$encodedUrl"
}


// --- Runtime entry: keeps the original signature for MainActivity ---
@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    MainAppScreenContent(
        onLogout = onLogout,
        authViewModel = authViewModel // Pass AuthViewModel
    )
}

// --- VM-free UI content used by both runtime and preview ---
@Composable
private fun MainAppScreenContent(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel? = null // Make nullable for Preview
) {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBars = items.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = { Text("OpenPlatform") },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Video.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Video.route) {
                VideoScreen(
                    onNavigateToUpload = { navController.navigate("upload") },
                    onNavigateToFullScreen = { videoUrl ->
                        navController.navigate(createFullScreenVideoRoute(videoUrl))
                    },
                    sharedViewModel = sharedViewModel
                )
            }
            composable(Screen.Progress.route) { ProgressScreen() }

            composable(Screen.Profile.route)  {
                // *** Modification Start ***
                if (authViewModel != null) {
                    val authState by authViewModel.uiState.collectAsState()
                    // 1. 在这里应用更改
                    // 我们现在传递 AuthViewModel，而不是让 ProfileScreen 创建自己的 ProfileViewModel
                    ProfileScreen(
                        authState = authState,
                        authViewModel = authViewModel // <-- *** MODIFIED ***
                    )
                } else {
                    // 2. 预览（Preview）部分也需要更新
                    // 它也不再需要那些 lambda
                    ProfileScreen(
                        authState = AuthUiState(
                            username = "preview_user",
                            email = "preview@example.com",
                            // fullName = "Preview Name", // <-- *** 移除 fullName ***
                            registeredDate = System.currentTimeMillis()
                        ),
                        authViewModel = null // <-- *** MODIFIED ***
                    )
                }
                // *** Modification End ***
            }

            composable("upload") {
                UploadVideoScreen(
                    onNavigateToVideo = { navController.popBackStack() },
                    sharedViewModel = sharedViewModel
                )
            }

            composable(
                route = FULLSCREEN_VIDEO_ROUTE_TEMPLATE,
                arguments = listOf(navArgument(VIDEO_URL_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString(VIDEO_URL_ARG) ?: ""
                val videoUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())

                FullScreenVideoScreen(
                    videoUrl = videoUrl,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// --- Preview without constructing a ViewModel in composition ---
@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    LearningDashboardTheme {
        MainAppScreenContent(
            onLogout = { /* no-op in preview */ },
            authViewModel = null // Pass null for Preview
        )
    }
}

// --- Helper functions for Preview below ---
// (These functions already exist in ProfileScreen.kt, but Preview needs them)

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

private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}