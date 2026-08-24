package com.example.planmanager.routine.service

import com.example.planmanager.presentation.api.routine.*
import com.example.planmanager.infrastructure.persistence.routine.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class RoutineService(
    private val groupRepository: RoutineGroupJpaRepository,
    private val itemRepository: RoutineItemJpaRepository // 💡 필요시 JpaRepository 인터페이스 생성 요망
) {
    // 1. 그룹 및 하위 항목 전체 조회 (조회 시점에 주기 도달 여부 체크 후 자동 초기화)
    fun getMyRoutineGroups(userId: Long, currentDate: LocalDate): List<RoutineGroupResponse> {
        val groups = groupRepository.findActiveGroupsWithItemsByUserId(userId)

        return groups.map { group ->
            // 💡 핵심: 오늘 날짜 기준으로 주기가 지났다면 하위 항목 체크박스 일괄 해제
            if (group.resetItemsIfPeriodReached(currentDate)) {
                groupRepository.save(group) // 변경된 상태 DB 반영
            }

            RoutineGroupResponse(
                id = group.id,
                title = group.title,
                intervalDays = group.intervalDays,
                items = group.items.map { item ->
                    RoutineItemResponse(item.id, item.title, item.isCompleted)
                }
            )
        }
    }

    // 2. 부모 그룹 생성
    fun createGroup(userId: Long, request: RoutineGroupCreateRequest): RoutineGroupResponse {
        val group = RoutineGroupEntity(
            userId = userId,
            title = request.title,
            intervalDays = request.intervalDays,
            lastResetDate = LocalDate.now()
        )
        val savedGroup = groupRepository.save(group)
        return RoutineGroupResponse(savedGroup.id, savedGroup.title, savedGroup.intervalDays, emptyList())
    }

    // 3. 부모 그룹에 자식 항목 추가
    fun addItemToGroup(userId: Long, groupId: Long, request: RoutineItemCreateRequest): RoutineItemResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw IllegalArgumentException("그룹을 찾을 수 없습니다.")
        if (group.userId != userId) throw SecurityException("권한이 없습니다.")

        val item = RoutineItemEntity(group = group, title = request.title)
        group.items.add(item)
        val savedGroup = groupRepository.save(group) // 영속성 전이(Cascade)로 자동 저장

        val savedItem = savedGroup.items.last()
        return RoutineItemResponse(savedItem.id, savedItem.title, savedItem.isCompleted)
    }

    // 4. 자식 항목 체크박스 토글
    fun toggleItem(userId: Long, itemId: Long) {
        val item = itemRepository.findByIdOrNull(itemId)
            ?: throw IllegalArgumentException("항목을 찾을 수 없습니다.")
        if (item.group.userId != userId) throw SecurityException("권한이 없습니다.")

        item.toggle()
    }
}