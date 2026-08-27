package com.example.planmanager.statistics.service

import com.example.planmanager.plan.repository.PlanRepository
import com.example.planmanager.presentation.api.statistics.StatisticsResponse
import org.springframework.stereotype.Service
import java.time.LocalDate
import org.springframework.transaction.annotation.Transactional

@Service
class StatisticsService(private val planRepository: PlanRepository) {

    @Transactional(readOnly = true)
    fun getPlanStatistics(userId: Long, startDate: LocalDate, endDate: LocalDate): StatisticsResponse {
        val total = planRepository.countTotalPlansByDateRange(userId, startDate, endDate)
        val completed = planRepository.countCompletedPlansByDateRange(userId, startDate, endDate)

        // 0으로 나누는(Divide by Zero) 오류 방지 로직
        val rate = if (total == 0L) 0.0 else (completed.toDouble() / total.toDouble()) * 100.0

        return StatisticsResponse(total, completed, Math.round(rate * 10.0) / 10.0) // 소수점 첫째 자리 반올림
    }
}