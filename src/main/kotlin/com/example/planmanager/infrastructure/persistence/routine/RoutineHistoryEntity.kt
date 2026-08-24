package com.example.planmanager.infrastructure.persistence.routine

import com.example.planmanager.domain.routine.RoutineHistory
import com.example.planmanager.domain.routine.RoutineStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "routine_histories",
    // CQRS 캘린더 조회를 위한 복합 인덱스 설정
    indexes = [Index(name = "idx_user_target_date", columnList = "user_id, target_date")]
)
class RoutineHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val routineId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val targetDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: RoutineStatus
) {
    companion object {
        fun fromDomain(history: RoutineHistory): RoutineHistoryEntity {
            return RoutineHistoryEntity(
                routineId = history.routineId,
                userId = history.userId,
                targetDate = history.targetDate,
                status = history.status
            )
        }
    }
}