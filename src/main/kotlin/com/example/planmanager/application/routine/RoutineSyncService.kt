package com.example.planmanager.application.routine

import com.example.planmanager.infrastructure.persistence.routine.RoutineGroupJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class RoutineSyncService(
    private val routineGroupJpaRepository: RoutineGroupJpaRepository
) {
    @Transactional
    fun syncRoutines(targetDate: LocalDate) {
        // 아직 리셋되지 않은(과거 시점의 lastResetDate를 가진) 활성 그룹 모두 조회
        val groups = routineGroupJpaRepository.findByIsActiveTrueAndLastResetDateBefore(targetDate)

        for (group in groups) {
            // 주기 도달 여부 계산 및 하위 아이템 일괄 false 처리 (Dirty Checking)
            group.resetItemsIfPeriodReached(targetDate)
        }
    }
}