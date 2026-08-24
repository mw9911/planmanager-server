package com.example.planmanager.infrastructure.persistence.routine

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface RoutineGroupJpaRepository : JpaRepository<RoutineGroupEntity, Long> {
    // 유저별 그룹 목록 조회 (Fetch Join으로 자식 아이템까지 한 번에 로드하여 N+1 문제 방지)
    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT g FROM RoutineGroupEntity g LEFT JOIN FETCH g.items WHERE g.userId = :userId AND g.isActive = true"
    )
    fun findActiveGroupsWithItemsByUserId(userId: Long): List<RoutineGroupEntity>

    // 자정(지연) 동기화를 위한 대상 그룹 조회
    fun findByIsActiveTrueAndLastResetDateBefore(targetDate: LocalDate): List<RoutineGroupEntity>
}