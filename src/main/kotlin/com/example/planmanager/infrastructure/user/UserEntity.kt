package com.example.planmanager.infrastructure.user

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String, // 💡 아이디(username) 필드 추가

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val passwordHash: String, // BCrypt로 암호화된 단방향 해시 저장

    @Column(nullable = false)
    val role: String = "ROLE_USER",

    @Column(nullable = false)
    var lastLoginDate: java.time.LocalDate = java.time.LocalDate.now()
)