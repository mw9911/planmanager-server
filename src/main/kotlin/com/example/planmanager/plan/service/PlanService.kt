package com.example.planmanager.plan.service

import com.example.planmanager.infrastructure.security.entity.RefreshTokenEntity
import com.example.planmanager.infrastructure.security.entity.RefreshTokenRepository
import com.example.planmanager.plan.entity.PlanEntity
import com.example.planmanager.plan.repository.PlanRepository
import com.example.planmanager.plan.dto.PlanCreateRequest
import com.example.planmanager.plan.dto.PlanResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull

@Service
@Transactional
class PlanService(
    private val planRepository: PlanRepository
) {
    // 💡 날짜 파라미터를 없애고, 날짜 오름차순으로 전체 데이터를 가져옵니다.
    @Transactional(readOnly = true)
    fun getAllPlans(userId: String): List<PlanResponse> {
        return planRepository.findAllByUserIdOrderByPlanDateAsc(userId)
            .map { PlanResponse(it) }
    }

    fun createPlan(userId: String, request: PlanCreateRequest): PlanResponse {
        val plan = PlanEntity(
            userId = userId.toLong(),
            title = request.title,
            planDate = request.planDate
        )
        val saved = planRepository.save(plan)
        return PlanResponse(saved)
    }

    fun togglePlanStatus(planId: Long, userId: String) {
        val plan = planRepository.findByIdOrNull(planId)
            ?: throw IllegalArgumentException("존재하지 않는 계획입니다.")
        if (plan.userId != userId.toLong()) throw SecurityException("권한이 없습니다.")
        plan.toggleComplete()
    }

    fun deletePlan(planId: Long, userId: String) {
        val plan = planRepository.findByIdOrNull(planId)
            ?: throw IllegalArgumentException("존재하지 않는 계획입니다.")
        if (plan.userId != userId.toLong()) throw SecurityException("권한이 없습니다.")
        planRepository.delete(plan)
    }
}