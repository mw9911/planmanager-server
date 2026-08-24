// 사용자의 실제 디렉토리 경로에 맞춘 패키지명
package com.example.planmanager.service.routine

import com.example.planmanager.routine.entity.RoutineEntity
import com.example.planmanager.routine.entity.RoutineRecordEntity
import com.example.planmanager.routine.repository.RoutineRecordRepository
import com.example.planmanager.routine.repository.RoutineRepository

// 💡 핵심: 패키지가 다르므로 실제 서비스 클래스를 명시적으로 Import 해야 합니다.
import com.example.planmanager.routine.service.RoutineLazySyncService

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class RoutineLazySyncServiceTest {

    @MockK
    lateinit var routineRepository: RoutineRepository

    @MockK
    lateinit var routineRecordRepository: RoutineRecordRepository

    @InjectMockKs
    lateinit var routineLazySyncService: RoutineLazySyncService

    @Test
    fun `최근 접속일이 3일 전인 유저가 로그인하면 3일치 루틴 레코드가 일괄 생성된다`() {
        val userId = 1L
        val lastLoginDate = LocalDate.parse("2026-08-20")
        val currentDate = LocalDate.parse("2026-08-23")

        val activeRoutines = listOf(
            RoutineEntity(id = 100L, userId = userId, title = "물 마시기", cycleType = "DAILY", status = "ACTIVE"),
            RoutineEntity(id = 101L, userId = userId, title = "스트레칭", cycleType = "DAILY", status = "ACTIVE")
        )

        every { routineRepository.findAllByUserIdAndStatus(userId, "ACTIVE") } returns activeRoutines

        val savedRecordsSlot = slot<List<RoutineRecordEntity>>()
        every { routineRecordRepository.saveAll(capture(savedRecordsSlot)) } returns emptyList()

        // 실행
        routineLazySyncService.syncMissingRoutines(userId, lastLoginDate, currentDate)

        // 💡 핵심: any() 대신 any<List<RoutineRecordEntity>>() 로 구체적 타입을 명시하여 컴파일 에러 해결
        verify(exactly = 1) { routineRecordRepository.saveAll(any<List<RoutineRecordEntity>>()) }

        val savedRecords = savedRecordsSlot.captured
        assertEquals(6, savedRecords.size)

        val targetDates = savedRecords.map { it.targetDate }.toSet()
        assertTrue(targetDates.containsAll(listOf(
            LocalDate.parse("2026-08-21"),
            LocalDate.parse("2026-08-22"),
            LocalDate.parse("2026-08-23")
        )))
    }

    @Test
    fun `최근 접속일과 현재 날짜가 동일하면 레코드를 생성하지 않는다`() {
        val userId = 1L
        val sameDate = LocalDate.parse("2026-08-23")

        routineLazySyncService.syncMissingRoutines(userId, sameDate, sameDate)

        verify(exactly = 0) { routineRepository.findAllByUserIdAndStatus(any(), any()) }
        // 💡 핵심: 여기도 타입 명시
        verify(exactly = 0) { routineRecordRepository.saveAll(any<List<RoutineRecordEntity>>()) }
    }
}