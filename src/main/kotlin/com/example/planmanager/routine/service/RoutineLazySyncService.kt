package com.example.planmanager.routine.service

import com.example.planmanager.routine.entity.RoutineRecordEntity
import com.example.planmanager.routine.repository.RoutineRecordRepository
import com.example.planmanager.routine.repository.RoutineRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class RoutineLazySyncService(
    private val routineRepository: RoutineRepository,
    private val routineRecordRepository: RoutineRecordRepository
) {
    @Transactional
    fun syncMissingRoutines(userId: Long, lastLoginDate: LocalDate, currentDate: LocalDate) {
        val daysBetween = ChronoUnit.DAYS.between(lastLoginDate, currentDate).toInt()
        if (daysBetween <= 0) return

        val activeRoutines = routineRepository.findAllByUserIdAndStatus(userId, "ACTIVE")
        if (activeRoutines.isEmpty()) return

        val missingDates = (1..daysBetween).map { lastLoginDate.plusDays(it.toLong()) }

        val missingRecords = missingDates.flatMap { date ->
            activeRoutines.mapNotNull { routine ->
                // 💡 생성일 기준으로 해당 날짜가 'N일 주기'에 정확히 맞아 떨어지는지 검사
                val daysSinceCreation = ChronoUnit.DAYS.between(routine.createdDate, date)

                if (daysSinceCreation % routine.intervalDays == 0L) {
                    RoutineRecordEntity(
                        routineId = routine.id,
                        userId = userId,
                        targetDate = date,
                        isCompleted = false
                    )
                } else {
                    null // 주기가 아닌 날은 건너뜀
                }
            }
        }

        if (missingRecords.isNotEmpty()) {
            routineRecordRepository.saveAll(missingRecords)
        }
    }
}