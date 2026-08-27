package com.example.planmanager.plan.controller

import com.example.planmanager.plan.service.PlanService
import com.example.planmanager.plan.dto.PlanCreateRequest
import com.example.planmanager.plan.dto.PlanResponse
import com.example.planmanager.plan.dto.PlanSyncRequest
import com.example.planmanager.plan.entity.PlanEntity
import com.example.planmanager.plan.repository.PlanRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/plans")
class PlanController(
    private val planService: PlanService,
    private val planRepository: PlanRepository
) {
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

    @PostMapping("/sync")
    fun syncOfflinePlans(
        @RequestAttribute("userId") userId: String,
        @RequestBody requests: List<PlanSyncRequest>
    ): ResponseEntity<String> {
        val entities = requests.map { req ->
            PlanEntity(
                userId = userId.toLong(),
                title = req.title,
                planDate = req.planDate
            ).apply {
                if (req.isCompleted) toggleComplete()
            }
        }
        planRepository.saveAll(entities)
        return ResponseEntity.ok("오프라인 데이터 동기화 완료")
    }
}