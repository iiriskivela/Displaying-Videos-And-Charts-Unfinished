package com.example.learningdashboard.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
// 1. Import PieEntry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
// 2. Import LocalTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 3. Define the time buckets
enum class TimeOfDay(val label: String) {
    NIGHT("Night"),
    MORNING("Morning"),
    AFTERNOON("Afternoon"),
    EVENING("Evening")
}

class UsageTimeViewModel : ViewModel() {

    // --- Data for Bar Chart ---
    private val _dayLabels = MutableStateFlow(generateDateLabels())
    val dayLabels: StateFlow<List<String>> = _dayLabels.asStateFlow()

    private val previousDaysUsage = mutableListOf(
        65f, 45f, 90f, 30f, 70f, 120f
    )
    private var todayUsageInSeconds = 0L
    private val _chartData = MutableStateFlow<List<BarEntry>>(emptyList())
    val chartData: StateFlow<List<BarEntry>> = _chartData.asStateFlow()

    // --- Data for Pie Chart ---
    // 4. Pre-defined average usage for the pie chart (demo data)
    private val historicalUsageByTimeOfDay = mutableMapOf(
        TimeOfDay.NIGHT to 30f,     // 30 min avg
        TimeOfDay.MORNING to 120f,  // 120 min avg
        TimeOfDay.AFTERNOON to 90f, // 90 min avg
        TimeOfDay.EVENING to 180f   // 180 min avg
    )

    // 5. Real-time usage for today, broken down by time of day
    private var todayUsageInSecondsByTimeOfDay = mutableMapOf(
        TimeOfDay.NIGHT to 0L,
        TimeOfDay.MORNING to 0L,
        TimeOfDay.AFTERNOON to 0L,
        TimeOfDay.EVENING to 0L
    )

    // 6. StateFlow for the pie chart data
    private val _pieChartData = MutableStateFlow<List<PieEntry>>(emptyList())
    val pieChartData: StateFlow<List<PieEntry>> = _pieChartData.asStateFlow()


    private var usageTimerJob: Job? = null

    init {
        // Load initial data for both charts
        updateChartData()
    }

    /**
     * Merges historical data with today's real-time data for both charts
     */
    private fun updateChartData() {
        // --- 1. Update Bar Chart Data ---
        val barEntries = mutableListOf<BarEntry>()
        previousDaysUsage.forEachIndexed { index, usageInMinutes ->
            barEntries.add(BarEntry(index.toFloat(), usageInMinutes))
        }
        val todayUsageInMinutes = todayUsageInSeconds / 60f
        barEntries.add(BarEntry(previousDaysUsage.size.toFloat(), todayUsageInMinutes))
        _chartData.value = barEntries

        // --- 2. Update Pie Chart Data ---
        val pieEntries = mutableListOf<PieEntry>()
        // Combine historical + today's data for each bucket
        for (timeOfDay in TimeOfDay.values()) {
            val historicalMinutes = historicalUsageByTimeOfDay.getOrDefault(timeOfDay, 0f)
            val todayMinutes = todayUsageInSecondsByTimeOfDay.getOrDefault(timeOfDay, 0L) / 60f
            val totalMinutes = historicalMinutes + todayMinutes

            // Add to chart only if there is data
            if (totalMinutes > 0) {
                // The PieEntry takes the value (minutes) and a label
                pieEntries.add(PieEntry(totalMinutes, timeOfDay.label))
            }
        }
        _pieChartData.value = pieEntries
    }

    /**
     * Starts tracking when the screen is visible
     */
    fun startTracking() {
        if (usageTimerJob?.isActive == true) return

        usageTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Every second

                // --- 7. Increment Total Usage (for Bar Chart) ---
                todayUsageInSeconds++

                // --- 8. Increment Bucket Usage (for Pie Chart) ---
                val currentBucket = getCurrentTimeOfDay()
                val currentSeconds = todayUsageInSecondsByTimeOfDay.getOrDefault(currentBucket, 0L)
                todayUsageInSecondsByTimeOfDay[currentBucket] = currentSeconds + 1L

                // --- 9. Update Both Charts ---
                updateChartData()
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

    // --- Helper Functions ---

    private fun generateDateLabels(): List<String> {
        val labels = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("M/d")
        for (i in 6 downTo 1) {
            val date = today.minusDays(i.toLong())
            labels.add(date.format(formatter))
        }
        labels.add("Today")
        return labels
    }

    // 10. Helper function to determine the current time bucket
    private fun getCurrentTimeOfDay(): TimeOfDay {
        val currentHour = LocalTime.now().hour
        return when (currentHour) {
            in 0..5 -> TimeOfDay.NIGHT
            in 6..11 -> TimeOfDay.MORNING
            in 12..17 -> TimeOfDay.AFTERNOON
            else -> TimeOfDay.EVENING // 18-23
        }
    }
}