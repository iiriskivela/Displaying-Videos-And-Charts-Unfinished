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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningdashboard.LearningProgressChart
import com.github.mikephil.charting.data.Entry

// --- Screen 2: Progress ---
@Composable
fun ProgressScreen() {
    // Sample Chart Data
    val chartData = listOf(
        Entry(1f, 10f),
        Entry(2f, 20f),
        Entry(3f, 15f),
        Entry(4f, 25f),
        Entry(5f, 30f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Learning Progress (Chart)",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        LearningProgressChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            data = chartData
        )
    }
}
