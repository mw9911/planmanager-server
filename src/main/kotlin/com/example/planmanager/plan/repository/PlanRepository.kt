// PlanRepository.kt (최종 수정본)
package com.example.planmanager.plan.repository

import com.example.planmanager.plan.entity.PlanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface PlanRepository : JpaRepository<PlanEntity, Long> {

    // 💡 치명적 버그 수정: userId: String -> userId: Long[cite: 20]
    fun findAllByUserIdAndPlanDate(userId: Long, planDate: LocalDate): List<PlanEntity>
    fun findAllByUserIdAndPlanDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): List<PlanEntity>
    fun findAllByUserIdOrderByPlanDateAsc(userId: Long): List<PlanEntity>

    // 💡 우선순위 2: 통계용 집계 쿼리 추가
    @Query("SELECT COUNT(p) FROM PlanEntity p WHERE p.userId = :userId AND p.planDate BETWEEN :startDate AND :endDate")
    fun countTotalPlansByDateRange(@Param("userId") userId: Long, @Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): Long

    @Query("SELECT COUNT(p) FROM PlanEntity p WHERE p.userId = :userId AND p.isCompleted = true AND p.planDate BETWEEN :startDate AND :endDate")
    fun countCompletedPlansByDateRange(@Param("userId") userId: Long, @Param("startDate") startDate: LocalDate, @Param("endDate") endDate: LocalDate): Long
}