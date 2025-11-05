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
            // --- 1. 创建和初始化 BarChart ---
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setDrawValueAboveBar(true) // 在柱子上方显示数值

                // --- 2. X轴 设置 ---
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    isGranularityEnabled = true
                    // 关键：设置X轴的标签
                    valueFormatter = IndexAxisValueFormatter(labels)
                }

                // --- 3. Y轴 设置 ---
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f // Y轴从0开始
                    // 添加 " min" 后缀
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()} min"
                        }
                    }
                }
                axisRight.isEnabled = false // 禁用右侧Y轴

                legend.isEnabled = false // 禁用图例
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

            val barData = BarData(dataSet)
            barData.barWidth = 0.6f // 设置柱子的宽度

            chart.data = barData
            chart.setFitBars(true) // 让柱子适应图表宽度
            chart.invalidate() // 刷新图表
        }
    )
}