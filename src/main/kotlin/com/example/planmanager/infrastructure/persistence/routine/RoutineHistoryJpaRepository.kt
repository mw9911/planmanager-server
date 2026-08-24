package com.example.planmanager.infrastructure.persistence.routine

import org.springframework.data.jpa.repository.JpaRepository

interface RoutineHistoryJpaRepository : JpaRepository<RoutineHistoryEntity, Long> {
    // 필요한 경우 추가 쿼리 메서드 정의 가능
}