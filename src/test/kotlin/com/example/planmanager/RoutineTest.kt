package com.example.planmanager

import com.example.planmanager.domain.routine.FrequencyType
import com.example.planmanager.domain.routine.Routine
import com.example.planmanager.domain.routine.RoutineStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class RoutineTest {

    @Test
    @DisplayName("지연 갱신: 3일간 미접속 시 3개의 SKIPPED 히스토리가 생성되고, 날짜가 갱신되어야 한다")
    fun calculatePendingHistories_LazyFallback() {
        // given: 2026년 8월 18일에 마지막으로 동기화된 일일 루틴
        val lastProcessedDate = LocalDate.of(2026, 8, 18)
        val currentDate = LocalDate.of(2026, 8, 21) // 3일 차이

        val routine = Routine(
            id = 1L,
            userId = 100L,
            title = "아침 조깅",
            frequencyType = FrequencyType.DAILY,
            lastProcessedDate = lastProcessedDate
        )

        // when: 8월 21일 접속하여 누락 히스토리 정산 로직 호출
        val pendingHistories = routine.calculatePendingHistories(currentDate)

        // then: 정확히 3개의 누락된 히스토리가 생성되어야 함
        assertEquals(3, pendingHistories.size, "3일 치 히스토리가 생성되어야 합니다.")

        // then: 각 히스토리의 날짜와 상태가 정확하게 매핑되었는지 검증
        assertEquals(LocalDate.of(2026, 8, 19), pendingHistories[0].targetDate)
        assertEquals(LocalDate.of(2026, 8, 20), pendingHistories[1].targetDate)
        assertEquals(LocalDate.of(2026, 8, 21), pendingHistories[2].targetDate)
        assertEquals(RoutineStatus.SKIPPED, pendingHistories[0].status)

        // then: 도메인 객체의 마지막 처리일이 오늘로 갱신되었는지 검증
        assertEquals(currentDate, routine.lastProcessedDate, "마지막 처리일이 8월 21일로 갱신되어야 합니다.")
    }
}