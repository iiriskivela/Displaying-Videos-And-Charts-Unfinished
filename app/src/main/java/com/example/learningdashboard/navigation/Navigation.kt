@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.learningdashboard.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.learningdashboard.auth.AuthViewModel
import com.example.learningdashboard.screens.ProfileScreen
import com.example.learningdashboard.screens.ProgressScreen
import com.example.learningdashboard.screens.VideoScreen
import com.example.learningdashboard.ui.theme.LearningDashboardTheme

// --- Navigation route definitions ---
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Video    : Screen("video",    "Study Videos",     Icons.Default.PlayArrow)
    data object Progress : Screen("progress", "Learning Process", Icons.Default.Star)
    data object Profile  : Screen("profile",  "Profile",          Icons.Default.Person)
}

private val items = listOf(
    Screen.Video,
    Screen.Progress,
    Screen.Profile
)

// --- Runtime entry: keeps the original signature for MainActivity ---
@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    // If someday you need data from the view model here, you can use it,
    // but for now we delegate to a VM-free content function for better preview/testability.
    MainAppScreenContent(onLogout = onLogout)
}

// --- VM-free UI content used by both runtime and preview ---
@Composable
private fun MainAppScreenContent(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Avoid building a large back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid relaunching the same destination
                                launchSingleTop = true
                                // Restore state when reselecting
                                restoreState = true
                            }
                        }
                    )
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
            composable(Screen.Video.route)    { VideoScreen() }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Profile.route)  {
                // If you want profile to use VM data later, pass what it needs as parameters.
                ProfileScreen()
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
            onLogout = { /* no-op in preview */ }
        )
    }
}
