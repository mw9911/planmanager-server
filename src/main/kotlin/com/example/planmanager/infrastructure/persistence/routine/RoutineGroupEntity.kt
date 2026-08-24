package com.example.planmanager.infrastructure.persistence.routine

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "routine_groups")
class RoutineGroupEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    var title: String, // 그룹 이름 (예: "4일 전신 운동 루틴")

    @Column(nullable = false)
    var intervalDays: Int, // 가변 주기 (예: 4)

    @Column(nullable = false)
    var lastResetDate: LocalDate, // 마지막 일괄 초기화가 발생한 기준일

    @Column(nullable = false)
    var isActive: Boolean = true,

    // 1:N 양방향 매핑 (부모가 삭제되면 자식도 삭제되는 영속성 전이 적용)
    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<RoutineItemEntity> = mutableListOf()
) {
    // 하위 항목 추가 편의 메서드
    fun addItem(title: String) {
        val item = RoutineItemEntity(group = this, title = title)
        items.add(item)
    }

    // 하위 항목 일괄 초기화 비즈니스 로직 (도메인 주도 설계)
    fun resetItemsIfPeriodReached(currentDate: LocalDate): Boolean {
        // 현재 날짜가 (마지막 초기화 날짜 + 주기) 이상이면 리셋 실행
        val nextResetDate = lastResetDate.plusDays(intervalDays.toLong())
        if (!currentDate.isBefore(nextResetDate)) {
            items.forEach { it.isCompleted = false }
            lastResetDate = currentDate
            return true
        }
        return false
    }
}