package com.example.planmanager.presentation.api.auth

data class SignUpRequest(
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String?,
    val message: String
)