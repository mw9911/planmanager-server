package com.example.planmanager.presentation.api.statistics

import com.example.planmanager.statistics.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/statistics")
class StatisticsController(private val statisticsService: StatisticsService) {

    @GetMapping("/plans")
    fun getStatistics(
        @RequestAttribute("userId") userId: Long, // JwtAuthenticationFilter에서 주입된 식별자
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate
    ): ResponseEntity<StatisticsResponse> {
        val stats = statisticsService.getPlanStatistics(userId, startDate, endDate)
        return ResponseEntity.ok(stats)
    }
}