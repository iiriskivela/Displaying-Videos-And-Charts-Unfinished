package com.example.learningdashboard // Make sure this is your package name

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LearningProgressChart(
    modifier: Modifier = Modifier,
    data: List<Entry>
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // Create and initialize Chart
            LineChart(context).apply {
                description.isEnabled = false
                isDragEnabled = true
                setScaleEnabled(true)
            }
        },
        update = { chart ->
            // When data changes, update Chart
            val dataSet = LineDataSet(data, "Learning Progress").apply { // <-- This line was changed
                color = Color.Blue.toArgb()
                valueTextColor = Color.Black.toArgb()
                setCircleColor(Color.Blue.toArgb())
                lineWidth = 2f
            }

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate() // Refresh chart
        }
    )
}
