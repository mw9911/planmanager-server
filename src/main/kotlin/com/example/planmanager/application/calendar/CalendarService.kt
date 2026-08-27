package com.example.planmanager.application.calendar

import com.example.planmanager.presentation.api.calendar.CalendarItemResponse
import com.example.planmanager.presentation.api.calendar.CalendarItemType
import com.example.planmanager.plan.repository.PlanRepository
import com.example.planmanager.routine.repository.RoutineRecordRepository
import com.example.planmanager.routine.repository.RoutineRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class CalendarService(
    private val planRepository: PlanRepository,
    private val routineRecordRepository: RoutineRecordRepository,
    private val routineRepository: RoutineRepository
) {
    fun getMonthlyCalendarItems(userId: Long, startDate: LocalDate, endDate: LocalDate): List<CalendarItemResponse> {
        val calendarItems = mutableListOf<CalendarItemResponse>()

        // 1. Plan 데이터 병합
        // 💡 주의: PlanEntity의 userId는 String 타입이므로 toString() 변환을 거칩니다.
        // 1. Plan 데이터 병합
        val plans = planRepository.findAllByUserIdAndPlanDateBetween(userId, startDate, endDate)
        calendarItems.addAll(
            plans.map {
                CalendarItemResponse(
                    id = it.id,
                    type = CalendarItemType.PLAN,
                    title = it.title,
                    targetDate = it.planDate, // 💡 실제 변수명인 planDate로 매핑
                    isCompleted = it.isCompleted
                )
            }
        )

        // 2. Routine Record 데이터 조회
        val routineRecords = routineRecordRepository.findAllByUserIdAndTargetDateBetween(userId, startDate, endDate)

        if (routineRecords.isNotEmpty()) {
            // 3. N+1 방지를 위한 Routine Master 일괄 조회 및 Map 캐싱
            val routineIds = routineRecords.map { it.routineId }.distinct()
            val routineMasters = routineRepository.findAllById(routineIds).associateBy { it.id }

            // 4. Routine 데이터 병합
            calendarItems.addAll(
                routineRecords.mapNotNull { record ->
                    val routineTitle = routineMasters[record.routineId]?.title ?: return@mapNotNull null
                    CalendarItemResponse(
                        id = record.id,
                        type = CalendarItemType.ROUTINE,
                        title = routineTitle,
                        targetDate = record.targetDate,
                        isCompleted = record.isCompleted
                    )
                }
            )
        }

        // 5. 날짜순(오름차순) 정렬 후 반환
        return calendarItems.sortedBy { it.targetDate }
    }
}