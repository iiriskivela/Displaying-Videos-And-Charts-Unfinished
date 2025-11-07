package com.example.learningdashboard.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// 1. 导入时间和日期格式化工具
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UsageTimeViewModel : ViewModel() {

    // 2. 创建一个函数来动态生成日期标签
    private fun generateDateLabels(): List<String> {
        val labels = mutableListOf<String>()
        val today = LocalDate.now()
        // 定义日期格式 (例如: "11/5")
        val formatter = DateTimeFormatter.ofPattern("M/d")

        // 循环添加今天之前的6天
        for (i in 6 downTo 1) {
            val date = today.minusDays(i.toLong())
            labels.add(date.format(formatter))
        }

        // 添加 "Today"
        labels.add("Today")
        return labels
    }

    // 3. 使用新函数来初始化 _dayLabels
    private val _dayLabels = MutableStateFlow(generateDateLabels())
    val dayLabels: StateFlow<List<String>> = _dayLabels.asStateFlow()

    // 2. Pre-defined usage data for previous days (unit: minutes)
    // (数据保持不变, 对应 6 天前 到 1 天前)
    private val previousDaysUsage = mutableListOf(
        65f,  // 6 days ago
        45f,  // 5 days ago
        90f,  // 4 days ago
        30f,  // 3 days ago
        70f,  // 2 days ago
        120f  // 1 day ago (Yesterday)
    )

    // 3. Real-time usage data for today (unit: seconds)
    private var todayUsageInSeconds = 0L

    // 4. Combined chart data flow
    private val _chartData = MutableStateFlow<List<BarEntry>>(emptyList())
    val chartData: StateFlow<List<BarEntry>> = _chartData.asStateFlow()

    private var usageTimerJob: Job? = null

    init {
        // Load initial data when ViewModel is initialized (Today's usage starts at 0)
        updateChartData()
    }

    /**
     * Merges historical data with today's real-time data
     */
    private fun updateChartData() {
        val entries = mutableListOf<BarEntry>()

        // Add data for previous days
        previousDaysUsage.forEachIndexed { index, usageInMinutes ->
            entries.add(BarEntry(index.toFloat(), usageInMinutes))
        }

        // Add data for today (converting seconds to minutes)
        val todayUsageInMinutes = todayUsageInSeconds / 60f
        entries.add(BarEntry(previousDaysUsage.size.toFloat(), todayUsageInMinutes))

        // Update the StateFlow
        _chartData.value = entries
    }

    /**
     * Starts tracking when the screen is visible
     */
    fun startTracking() {
        if (usageTimerJob?.isActive == true) return

        usageTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Every second
                todayUsageInSeconds++ // Increment today's seconds
                updateChartData() // Update the chart data
            }
        }
    }

    /**
     * Stops tracking when the screen is not visible
     */
    fun stopTracking() {
        usageTimerJob?.cancel()
        usageTimerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTracking()
    }
}