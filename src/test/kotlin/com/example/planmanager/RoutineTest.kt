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
    @DisplayName("당일 접속: 마지막 처리일과 오늘이 같은 날일 때, 생성되는 누락 히스토리는 0개여야 한다")
    fun calculatePendingHistories_SameDay() {
        // given: 오늘(2026년 9월 13일) 이미 처리된 일일 루틴
        val today = LocalDate.of(2026, 9, 13)

        val routine = Routine(
            id = 1L,
            userId = 100L,
            title = "알고리즘 문제 풀기",
            frequencyType = FrequencyType.DAILY,
            lastProcessedDate = today
        ) //

        // when: 동일한 날짜로 정산 로직 호출
        val pendingHistories = routine.calculatePendingHistories(today) //

        // then: 경과일이 없으므로 생성된 히스토리는 0개여야 함
        assertEquals(0, pendingHistories.size, "당일 접속 시 히스토리가 생성되지 않아야 합니다.") //[cite: 1]

        // then: 마지막 처리일은 여전히 오늘로 유지되어야 함
        assertEquals(today, routine.lastProcessedDate, "마지막 처리일은 변경 없이 오늘로 유지되어야 합니다.") //[cite: 1]
    }

    @Test
    @DisplayName("월 전환(Month Transition): 8월 30일에서 9월 2일로 넘어갈 때, 정확히 3개의 SKIPPED 히스토리가 생성되어야 한다")
    fun calculatePendingHistories_MonthTransition() {
        // given: 8월 30일에 마지막으로 처리된 일일 루틴
        val lastProcessedDate = LocalDate.of(2026, 8, 30)
        val currentDate = LocalDate.of(2026, 9, 2)

        val routine = Routine(
            id = 2L,
            userId = 200L,
            title = "AWS 아키텍처 복습",
            frequencyType = FrequencyType.DAILY,
            lastProcessedDate = lastProcessedDate
        ) //[cite: 1]

        // when: 9월 2일 접속하여 누락 히스토리 정산 로직 호출
        val pendingHistories = routine.calculatePendingHistories(currentDate) //[cite: 1]

        // then: 8/31, 9/1, 9/2 총 3일 치 히스토리가 생성되어야 함
        assertEquals(3, pendingHistories.size, "월이 넘어가더라도 정확히 3개의 히스토리가 생성되어야 합니다.") //[cite: 1]

        // then: 윤달/월별 일수 계산(8월은 31일까지)이 정확히 반영되어 타겟 날짜가 매핑되었는지 검증
        assertEquals(LocalDate.of(2026, 8, 31), pendingHistories[0].targetDate) //[cite: 1]
        assertEquals(LocalDate.of(2026, 9, 1), pendingHistories[1].targetDate) //[cite: 1]
        assertEquals(LocalDate.of(2026, 9, 2), pendingHistories[2].targetDate) //[cite: 1]

        // then: 누락된 히스토리의 상태는 SKIPPED로 지정되어야 함
        assertEquals(RoutineStatus.SKIPPED, pendingHistories[0].status) //[cite: 1]

        // then: 도메인 객체의 마지막 처리일이 9월 2일로 갱신되었는지 검증
        assertEquals(currentDate, routine.lastProcessedDate, "마지막 처리일이 9월 2일로 갱신되어야 합니다.") //[cite: 1]
    }
    @Test
    @DisplayName("윤년 처리(Leap Year): 2028년 2월 28일에서 3월 1일로 넘어갈 때, 2월 29일을 포함하여 정확히 2개의 SKIPPED 히스토리가 생성되어야 한다")
    fun calculatePendingHistories_LeapYear() {
        // given: 윤년인 2028년 2월 28일에 마지막으로 처리된 일일 루틴
        val lastProcessedDate = LocalDate.of(2028, 2, 28)
        val currentDate = LocalDate.of(2028, 3, 1)

        val routine = Routine(
            id = 3L,
            userId = 300L,
            title = "윤년 테스트 로직 검증",
            frequencyType = FrequencyType.DAILY,
            lastProcessedDate = lastProcessedDate
        )

        // when: 2028년 3월 1일 접속하여 누락 히스토리 정산 로직 호출
        val pendingHistories = routine.calculatePendingHistories(currentDate)

        // then: 2/29, 3/1 총 2일 치 히스토리가 생성되어야 함 (기존 3에서 2로 수정)
        assertEquals(2, pendingHistories.size, "윤년인 2월 29일을 포함하여 정확히 2개의 히스토리가 생성되어야 합니다.")

        // then: 2월 29일(윤일)이 정확히 계산되어 타겟 날짜에 포함되었는지 검증 (기존 28일에서 29일로 수정)
        assertEquals(LocalDate.of(2028, 2, 29), pendingHistories[0].targetDate)
        assertEquals(LocalDate.of(2028, 3, 1), pendingHistories[1].targetDate)

        // then: 누락된 히스토리의 상태는 SKIPPED로 지정되어야 함
        assertEquals(RoutineStatus.SKIPPED, pendingHistories[0].status)

        // then: 도메인 객체의 마지막 처리일이 3월 1일로 갱신되었는지 검증
        assertEquals(currentDate, routine.lastProcessedDate, "마지막 처리일이 3월 1일로 갱신되어야 합니다.")
    }
}