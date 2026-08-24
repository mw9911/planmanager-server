package com.example.planmanager.routine.repository

import com.example.planmanager.routine.entity.RoutineEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoutineRepository : JpaRepository<RoutineEntity, Long> {
    // 💡 아래의 ACTIVE 상태 조회 메서드만 남겨두고, targetDate가 포함된 메서드는 모두 삭제하십시오.
    fun findAllByUserIdAndStatus(userId: Long, status: String): List<RoutineEntity>
}