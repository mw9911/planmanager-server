package com.example.planmanager.routine.repository

import com.example.planmanager.routine.entity.RoutineRecordEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface RoutineRecordRepository : JpaRepository<RoutineRecordEntity, Long> {
    fun findAllByUserIdAndTargetDate(userId: Long, targetDate: LocalDate): List<RoutineRecordEntity>

    // 💡 달력 월간 조회를 위한 Between 쿼리는 반드시 이 파일에 있어야 합니다.
    fun findAllByUserIdAndTargetDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate): List<RoutineRecordEntity>
}