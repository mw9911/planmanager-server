package com.example.planmanager.infrastructure.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?

    // 💡 아이디 또는 이메일 중 하나라도 일치하는 레코드 조회 메서드 추가
    fun findByUsernameOrEmail(username: String, email: String): UserEntity?

    // 배치용 쿼리
    fun findByLastLoginDateGreaterThanEqual(date: LocalDate, pageable: Pageable): Page<UserEntity>
}