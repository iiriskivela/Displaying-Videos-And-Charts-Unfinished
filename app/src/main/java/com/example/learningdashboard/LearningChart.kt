package com.example.learningdashboard // Make sure this is your package name

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
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
            // 创建和初始化图表
            LineChart(context).apply {
                description.isEnabled = false
                isDragEnabled = true
                setScaleEnabled(true)
                setDrawGridBackground(false)

                // X 轴设置
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f // X轴最小间隔

                // Y 轴设置
                axisLeft.setDrawGridLines(true) // 显示Y轴网格线
                axisRight.isEnabled = false // 禁用右侧Y轴

                legend.isEnabled = true // 显示图例
            }
        },
        update = { chart ->
            // --- 优化的更新逻辑 ---

            // 检查图表是否已经有数据
            if (chart.data != null && chart.data.dataSetCount > 0) {
                // 获取已存在的数据集
                val dataSet = chart.data.getDataSetByIndex(0) as LineDataSet

                // 直接更新数据集的值
                dataSet.values = data

                // 通知图表数据已更改
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {
                // 如果没有数据，创建新的数据集
                val dataSet = LineDataSet(data, "Usage Time (s)").apply {
                    color = Color.Blue.toArgb()
                    valueTextColor = Color.Black.toArgb()
                    setCircleColor(Color.Blue.toArgb())
                    circleRadius = 3f
                    setDrawValues(false) // 不在每个点上绘制数值，保持图表简洁
                    lineWidth = 2f
                    // 使用平滑曲线
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                val lineData = LineData(dataSet)
                chart.data = lineData
            }

            // 自动滚动到最新的数据点
            chart.moveViewToX(data.lastOrNull()?.x ?: 0f)

            // 刷新图表
            chart.invalidate()
        }
    )
}