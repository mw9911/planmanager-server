package com.example.planmanager.application.routine

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class RoutineSchedulerTest {

    @Test
    @DisplayName("스케줄러: 자정 정산 메서드가 호출되면 서비스의 syncRoutines가 오늘 날짜로 실행되어야 한다")
    fun midnightSyncExecution() {
        // given
        val routineSyncService: RoutineSyncService = mockk()
        val routineScheduler = RoutineScheduler(routineSyncService)

        every { routineSyncService.syncRoutines(any()) } just runs

        // when: 자정 스케줄러 메서드 수동 호출 (시간 경과 시뮬레이션)
        routineScheduler.runMidnightRoutineSync()

        // then: 서비스가 오늘 날짜를 파라미터로 하여 정확히 1번 호출되었는지 검증
        verify(exactly = 1) { routineSyncService.syncRoutines(LocalDate.now()) }
    }
}