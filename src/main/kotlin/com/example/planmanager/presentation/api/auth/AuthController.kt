package com.example.planmanager.presentation.api.auth

import com.example.planmanager.application.auth.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<AuthResponse> {
        return try {
            authService.signUp(request.email, request.password)
            ResponseEntity.ok(AuthResponse(token = null, message = "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(token = null, message = e.message ?: "회원가입 실패"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val token = authService.login(request.email, request.password)
            ResponseEntity.ok(AuthResponse(token = token, message = "로그인 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(token = null, message = e.message ?: "로그인 실패"))
        }
    }
}