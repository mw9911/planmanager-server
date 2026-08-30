package com.example.planmanager.presentation.api.auth

data class SignUpRequest(
    val username: String, // 💡 회원가입 시 아이디 추가
    val email: String,
    val password: String
)

data class LoginRequest(
    val loginId: String, // 💡 email 필드를 loginId로 범용화
    val password: String
)

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val message: String
)