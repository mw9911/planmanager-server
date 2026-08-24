package com.example.planmanager.plan.controller

import com.example.planmanager.plan.service.PlanService
import com.example.planmanager.plan.dto.PlanCreateRequest
import com.example.planmanager.plan.dto.PlanResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/plans")
class PlanController(
    private val planService: PlanService
) {
    // 💡 RequestParam(date) 제거
    @GetMapping
    fun getAllPlans(@RequestAttribute("userId") userId: String): ResponseEntity<List<PlanResponse>> {
        return ResponseEntity.ok(planService.getAllPlans(userId))
    }

    @PostMapping
    fun createPlan(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: PlanCreateRequest
    ): ResponseEntity<PlanResponse> {
        return ResponseEntity.ok(planService.createPlan(userId, request))
    }

    @PatchMapping("/{planId}/toggle")
    fun togglePlan(
        @RequestAttribute("userId") userId: String,
        @PathVariable planId: Long
    ): ResponseEntity<Void> {
        planService.togglePlanStatus(planId, userId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{planId}")
    fun deletePlan(
        @RequestAttribute("userId") userId: String,
        @PathVariable planId: Long
    ): ResponseEntity<Void> {
        planService.deletePlan(planId, userId)
        return ResponseEntity.ok().build()
    }
}