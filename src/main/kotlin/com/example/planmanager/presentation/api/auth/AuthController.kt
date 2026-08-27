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
            // 💡 token 대신 accessToken과 refreshToken에 각각 null 부여
            ResponseEntity.ok(AuthResponse(accessToken = null, refreshToken = null, message = "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            // 💡 예외 처리부도 동일하게 수정
            ResponseEntity.badRequest().body(AuthResponse(accessToken = null, refreshToken = null, message = e.message ?: "회원가입 실패"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val (accessToken, refreshToken) = authService.login(request.email, request.password)
            ResponseEntity.ok(AuthResponse(accessToken, refreshToken, "로그인 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(null, null, e.message ?: "로그인 실패"))
        }
    }

    @PostMapping("/reissue")
    fun reissue(@RequestHeader("Refresh-Token") refreshToken: String): ResponseEntity<AuthResponse> {
        return try {
            val (newAccessToken, newRefreshToken) = authService.reissueToken(refreshToken)
            ResponseEntity.ok(AuthResponse(newAccessToken, newRefreshToken, "토큰 재발급 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).body(AuthResponse(null, null, e.message ?: "재발급 실패"))
        }
    }
}