package com.example.planmanager.presentation.api.routine

// 1. 그룹(마스터) 관련 DTO
data class RoutineGroupCreateRequest(
    val title: String,
    val intervalDays: Int
)

data class RoutineGroupResponse(
    val id: Long,
    val title: String,
    val intervalDays: Int,
    val items: List<RoutineItemResponse>
)

// 2. 자식(항목) 관련 DTO
data class RoutineItemCreateRequest(
    val title: String
)

data class RoutineItemResponse(
    val id: Long,
    val title: String,
    val isCompleted: Boolean
)