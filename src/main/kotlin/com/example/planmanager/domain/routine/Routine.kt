package com.example.planmanager.domain.routine

import java.time.LocalDate

enum class FrequencyType {
    DAILY, WEEKLY
}

enum class RoutineStatus {
    COMPLETED, SKIPPED, FAILED
}

// 루틴 수행 이력 스냅샷 (추후 DB의 routine_histories 테이블과 매핑됨)
class RoutineHistory(
    val routineId: Long,
    val userId: Long,
    val targetDate: LocalDate,
    val status: RoutineStatus
)

// 루틴 마스터 도메인 객체
class Routine(
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val frequencyType: FrequencyType,
    var lastProcessedDate: LocalDate,
    var isActive: Boolean = true
) {
    /**
     * 핵심 비즈니스 로직: 지연 갱신(Lazy Fallback)
     * 마지막 처리일과 접속일(현재)을 비교하여 누락된 기간의 'SKIPPED' 히스토리를 반환합니다.
     */
    fun calculatePendingHistories(currentDate: LocalDate): List<RoutineHistory> {
        val pendingHistories = mutableListOf<RoutineHistory>()
        var processDate = lastProcessedDate.plusDays(1)

        // 마지막 처리일 다음날부터 현재 날짜까지 반복 검사
        while (!processDate.isAfter(currentDate)) {
            if (frequencyType == FrequencyType.DAILY) {
                pendingHistories.add(
                    RoutineHistory(
                        routineId = this.id,
                        userId = this.userId,
                        targetDate = processDate,
                        status = RoutineStatus.SKIPPED
                    )
                )
            }
            // 주간(WEEKLY) 등의 알고리즘은 필요시 여기에 추가 분기
            processDate = processDate.plusDays(1)
        }

        // 상태(히스토리) 생성 후, 루틴의 마지막 갱신일을 현재 날짜로 변경
        if (pendingHistories.isNotEmpty()) {
            this.lastProcessedDate = currentDate
        }

        return pendingHistories
    }
}