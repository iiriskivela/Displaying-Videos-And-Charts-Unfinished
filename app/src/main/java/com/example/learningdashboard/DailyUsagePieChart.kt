package com.example.learningdashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun DailyUsagePieChart(
    modifier: Modifier = Modifier,
    data: List<PieEntry>
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PieChart(context).apply {

                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(Color.Transparent.toArgb())
                setDrawEntryLabels(false)
                setUsePercentValues(true)

                // --- LEGEND CONFIGURATION ---
                legend.apply {
                    isEnabled = true

                    // TODO (STUDENT TASK 1):
                    // Set the legend to be **vertical** and on the center and positioned on the right side.
                    // verticalAlignment = TODO
                    // horizontalAlignment = TODO
                    // orientation = TODO
                    //
                    // Example expected result: Legend appears on the right side of the chart.

                    setDrawInside(false)

                    // --- 3. Reduce legend text and shape size ---
                    form = Legend.LegendForm.SQUARE
                    formSize = 8f                  // <-- Reduced (was 10f)
                    textSize = 10f                 // <-- Reduced (was 12f)
                    yEntrySpace = 3f               // <-- Reduced entry spacing (was 5f)

                    // TODO (STUDENT TASK 2):
                    // Enable word wrapping to prevent clipping on small screens.
                }
            }
        },
        update = { chart ->
            // --- 4. Update data ---
            val dataSet = PieDataSet(data, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                sliceSpace = 2f // Reduce slice spacing

                // --- 5. Reduce percentage value (Value) format ---
                valueTextColor = Color.Black.toArgb()
                valueTextSize = 10f // <-- Reduced (was 12f)
                valueFormatter = PercentFormatter(chart)

                // --- 6. Adjust value lines ---
                valueLinePart1OffsetPercentage = 100f
                valueLinePart1Length = 0.4f // <-- Shortened
                valueLinePart2Length = 0.4f // <-- Shortened
                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            }

            val pieData = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(chart))
                setValueTextSize(10f) // <-- Reduced (was 12f)
                setValueTextColor(Color.Black.toArgb())
            }

            // --- 7. Adjust chart offsets ---
            // Reduce right offset to make more space for the pie chart
            // (left, top, right, bottom)
            chart.setExtraOffsets(20f, 5f, 45f, 5f) // <-- Right offset reduced (was 100f)

            chart.data = pieData
            chart.invalidate() // Refresh the chart
        }
    )
}