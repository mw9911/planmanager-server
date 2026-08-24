package com.example.planmanager.infrastructure.persistence.routine

import jakarta.persistence.*

@Entity
@Table(name = "routine_items")
class RoutineItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: RoutineGroupEntity, // 부모 참조

    @Column(nullable = false)
    var title: String, // 개별 항목 이름 (예: "푸시업 100개")

    @Column(nullable = false)
    var isCompleted: Boolean = false // 당일(주기 내) 완료 여부
) {
    fun toggle() {
        this.isCompleted = !this.isCompleted
    }
}