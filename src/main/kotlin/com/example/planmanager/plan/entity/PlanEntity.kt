package com.example.planmanager.plan.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "plans")
class PlanEntity(
    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var planDate: LocalDate
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Column(nullable = false)
    var isCompleted: Boolean = false
        protected set // all-open 플러그인 충돌 방지를 위해 protected 사용

    fun toggleComplete() {
        this.isCompleted = !this.isCompleted
    }
}