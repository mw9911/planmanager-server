package com.example.planmanager.presentation.api.routine

import com.example.planmanager.routine.service.RoutineService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/routines")
class RoutineController(
    private val routineService: RoutineService
) {
    // 그룹 및 항목 목록 조회
    @GetMapping
    fun getRoutines(
        @RequestAttribute("userId") userId: String,
        @RequestParam date: LocalDate
    ): ResponseEntity<List<RoutineGroupResponse>> {
        return ResponseEntity.ok(routineService.getMyRoutineGroups(userId.toLong(), date))
    }

    // 1. 마스터 그룹 생성
    @PostMapping("/groups")
    fun createGroup(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: RoutineGroupCreateRequest
    ): ResponseEntity<RoutineGroupResponse> {
        return ResponseEntity.ok(routineService.createGroup(userId.toLong(), request))
    }

    // 2. 특정 그룹 내에 자식 항목 생성
    @PostMapping("/groups/{groupId}/items")
    fun addItem(
        @RequestAttribute("userId") userId: String,
        @PathVariable groupId: Long,
        @RequestBody request: RoutineItemCreateRequest
    ): ResponseEntity<RoutineItemResponse> {
        return ResponseEntity.ok(routineService.addItemToGroup(userId.toLong(), groupId, request))
    }

    // 3. 자식 항목 상태 토글
    @PatchMapping("/items/{itemId}/toggle")
    fun toggleItem(
        @RequestAttribute("userId") userId: String,
        @PathVariable itemId: Long
    ): ResponseEntity<Void> {
        routineService.toggleItem(userId.toLong(), itemId)
        return ResponseEntity.ok().build()
    }
}