package com.example.planmanager.routine.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "routines")
class RoutineEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    var title: String,

    // 💡 변경: 고정 문자열(cycleType) 대신 N일 간격(intervalDays) 사용
    @Column(nullable = false)
    var intervalDays: Int,

    @Column(nullable = false)
    var status: String = "ACTIVE",

    // 💡 변경: 배치 작업에서 주기를 계산하기 위한 생성일 기준점 추가
    @Column(nullable = false)
    val createdDate: LocalDate = LocalDate.now()
)