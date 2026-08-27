// StatisticsResponse.kt
package com.example.planmanager.presentation.api.statistics

data class StatisticsResponse(
    val totalPlans: Long,
    val completedPlans: Long,
    val completionRate: Double // (completedPlans / totalPlans) * 100
)