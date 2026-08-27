// RefreshTokenEntity.kt
package com.example.planmanager.infrastructure.security.entity

import jakarta.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(
    @Id
    @Column(name = "user_id")
    val userId: Long, // 1유저 1기기 정책 적용 (중복 로그인 방지)

    @Column(nullable = false, length = 512)
    var token: String
) {
    fun updateToken(newToken: String) {
        this.token = newToken
    }
}