package com.example.learningdashboard.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Import the viewModel() function
import androidx.lifecycle.viewmodel.compose.viewModel
// 1. Import the BarChart Composable
import com.example.learningdashboard.DailyUsageBarChart
// Import the ViewModel
import com.example.learningdashboard.ViewModels.UsageTimeViewModel
// 2. Remove Entry import (no longer needed)
// import com.github.mikephil.charting.data.Entry

// --- Screen 2: Progress ---
@Composable
fun ProgressScreen(
    // Get ViewModel instance via compose-lifecycle library
    viewModel: UsageTimeViewModel = viewModel()
) {
    // 3. Observe chart data (This will be List<BarEntry>)
    val chartData by viewModel.chartData.collectAsState()
    // 4. Observe X-axis labels (e.g., "Mon", "Tue", "Today")
    val labels by viewModel.dayLabels.collectAsState()

    // Use DisposableEffect to manage the timer's lifecycle
    // (This logic remains the same)
    DisposableEffect(key1 = viewModel) {
        viewModel.startTracking() // Start tracking

        onDispose {
            viewModel.stopTracking() // Stop tracking
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daily Usage (Minutes)", // 5. Title
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp)) // 6. Add a bit of space
        // 7. Use DailyUsageBarChart
        DailyUsageBarChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp), // Increase height to accommodate labels
            // 8. Pass BarChart data and labels
            data = chartData,
            labels = labels
        )
    }
}