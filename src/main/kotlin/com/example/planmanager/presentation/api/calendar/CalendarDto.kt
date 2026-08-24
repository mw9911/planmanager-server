package com.example.planmanager.presentation.api.calendar

import java.time.LocalDate

enum class CalendarItemType {
    PLAN, ROUTINE
}

data class CalendarItemResponse(
    val id: Long, // Plan의 ID 또는 RoutineRecord의 ID
    val type: CalendarItemType, // 클라이언트에서 아이콘이나 색상을 구분할 타입
    val title: String,
    val targetDate: LocalDate,
    val isCompleted: Boolean
)