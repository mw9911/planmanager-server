package com.example.planmanager.routine.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "routine_records")
class RoutineRecordEntity(
    @Column(nullable = false)
    val routineId: Long,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val targetDate: LocalDate,

    @Column(nullable = false)
    var isCompleted: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Column(nullable = true)
    var completedAt: LocalDateTime? = null
}