package com.example.planmanager.presentation.api.calendar

import com.example.planmanager.application.calendar.CalendarService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/calendar")
class CalendarController(
    private val calendarService: CalendarService
) {
    @GetMapping
    fun getCalendarItems(
        @RequestAttribute("userId") userId: String,
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate
    ): ResponseEntity<List<CalendarItemResponse>> {

        val items = calendarService.getMonthlyCalendarItems(userId.toLong(), startDate, endDate)
        return ResponseEntity.ok(items)
    }
}