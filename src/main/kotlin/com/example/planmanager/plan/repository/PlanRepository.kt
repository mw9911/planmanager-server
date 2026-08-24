package com.example.planmanager.plan.repository

import com.example.planmanager.plan.entity.PlanEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface PlanRepository : JpaRepository<PlanEntity, Long> {
    fun findAllByUserIdAndPlanDate(userId: String, planDate: LocalDate): List<PlanEntity>

    // 💡 추가된 달력 월간 조회 메서드
    fun findAllByUserIdAndPlanDateBetween(userId: String, startDate: LocalDate, endDate: LocalDate): List<PlanEntity>

    // 💡 [핵심 추가] 전체 계획을 날짜 오름차순으로 가져오기 위한 메서드
    fun findAllByUserIdOrderByPlanDateAsc(userId: String): List<PlanEntity>
}