package com.example.planmanager.infrastructure.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface UserRepository : JpaRepository<UserEntity, Long> {
    // 💡 누락된 기존 메서드 복구
    fun findByEmail(email: String): UserEntity?

    // 배치용 쿼리
    fun findByLastLoginDateGreaterThanEqual(date: LocalDate, pageable: Pageable): Page<UserEntity>
}