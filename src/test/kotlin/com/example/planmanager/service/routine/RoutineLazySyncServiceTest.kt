package com.example.planmanager.service.routine

import com.example.planmanager.routine.entity.RoutineEntity
import com.example.planmanager.routine.entity.RoutineRecordEntity
import com.example.planmanager.routine.repository.RoutineRecordRepository
import com.example.planmanager.routine.repository.RoutineRepository
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
            // 💡 수정된 부분: cycleType = "DAILY" 삭제 및 intervalDays = 1 추가
            RoutineEntity(id = 100L, userId = userId, title = "물 마시기", intervalDays = 1, status = "ACTIVE"),
            RoutineEntity(id = 101L, userId = userId, title = "스트레칭", intervalDays = 1, status = "ACTIVE")
        )

        every { routineRepository.findAllByUserIdAndStatus(userId, "ACTIVE") } returns activeRoutines

        val savedRecordsSlot = slot<List<RoutineRecordEntity>>()
        every { routineRecordRepository.saveAll(capture(savedRecordsSlot)) } returns emptyList()

        // 실행
        routineLazySyncService.syncMissingRoutines(userId, lastLoginDate, currentDate)

        verify(exactly = 1) { routineRecordRepository.saveAll(any<List<RoutineRecordEntity>>()) }

        val savedRecords = savedRecordsSlot.captured
        assertEquals(6, savedRecords.size) // 2개의 루틴 * 3일 = 6개

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
        verify(exactly = 0) { routineRecordRepository.saveAll(any<List<RoutineRecordEntity>>()) }
    }
}