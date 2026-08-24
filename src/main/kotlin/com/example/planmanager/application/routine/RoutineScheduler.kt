package com.example.planmanager.application.routine

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RoutineScheduler(
    private val routineSyncService: RoutineSyncService
) {
    /**
     * 매일 자정(00시 00분 00초)에 실행되는 자동화 스케줄러
     * cron 표현식: "초 분 시 일 월 요일" (0 0 0 * * * = 매일 자정)
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    fun runMidnightRoutineSync() {
        val today = LocalDate.now()

        // 자정 시점에 '오늘' 날짜를 기준으로 어제까지의 누락분을 일괄 정산
        routineSyncService.syncRoutines(today)
    }
}