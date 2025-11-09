package com.example.learningdashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun DailyUsageBarChart(
    modifier: Modifier = Modifier,
    data: List<BarEntry>,
    labels: List<String>
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {

                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawValueAboveBar(true)

                // --- X Axis Configuration ---
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    isGranularityEnabled = true

                    // TODO (STUDENT TASK 1):
                    // Assign `IndexAxisValueFormatter(labels)` to `valueFormatter`
                    // This should display the day labels beneath each bar.
                    // Example result: Mon, Tue, Wed, Thu, Fri, Sat, Today
                    //
                    // Put it below:
                    // TODO
                }

                // --- Y Axis Configuration ---
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f

                    // TODO (STUDENT TASK 2):
                    // Implement a ValueFormatter that returns the value in minutes
                    // Format: "X min" (e.g., 45 → "45 min")
                    // Uncomment below when implemented:
                    //
                    // valueFormatter = object : ValueFormatter() {
                    //     override fun getFormattedValue(value: Float): String {
                    //         return TODO
                    //     }
                    // }
                }

                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->

            // --- 4. 更新数据 ---
            val dataSet = BarDataSet(data, "Daily Usage").apply {
                color = Color(0xFF5C6BC0).toArgb() // 漂亮的靛蓝色
                valueTextColor = Color.Black.toArgb()
                valueTextSize = 10f
                // 柱子上的数值也使用 Y 轴的格式化
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        // 仅当数值大于0时显示
                        return if (value > 0) value.toInt().toString() else ""
                    }
                }
            }

            // TODO (STUDENT TASK 3):
            // Create a BarData object called barData using the `dataSet` you implemented above:
            // Put it below here:
            // TODO
            //
            // Then set bar width to 0.6f. Put it below:
            // TODO

            chart.data = barData

            chart.setFitBars(true)

            chart.invalidate()
        }
    )
}
