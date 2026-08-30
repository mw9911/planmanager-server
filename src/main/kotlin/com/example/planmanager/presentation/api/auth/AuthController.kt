package com.example.planmanager.presentation.api.auth

import com.example.planmanager.application.auth.AuthService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
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
            // 💡 request.username 전달 추가
            authService.signUp(request.username, request.email, request.password)
            ResponseEntity.ok(AuthResponse(accessToken = null, refreshToken = null, message = "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(accessToken = null, refreshToken = null, message = e.message ?: "회원가입 실패"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            // 💡 request.loginId 전달로 변경
            val (accessToken, refreshToken) = authService.login(request.loginId, request.password)

            val cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(14 * 24 * 60 * 60)
                .build()

            ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse(accessToken, refreshToken, "로그인 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(null, null, e.message ?: "로그인 실패"))
        }
    }

    @PostMapping("/reissue")
    fun reissue(
        @RequestHeader("Refresh-Token", required = false) headerToken: String?,
        @CookieValue(name = "refreshToken", required = false) cookieToken: String?
    ): ResponseEntity<AuthResponse> {

        val refreshToken = headerToken ?: cookieToken

        if (refreshToken.isNullOrBlank()) {
            return ResponseEntity.status(401).body(AuthResponse(null, null, "Refresh Token이 존재하지 않습니다."))
        }

        return try {
            val (newAccessToken, newRefreshToken) = authService.reissueToken(refreshToken)

            val cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(14 * 24 * 60 * 60)
                .build()

            ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse(newAccessToken, newRefreshToken, "토큰 재발급 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).body(AuthResponse(null, null, e.message ?: "재발급 실패"))
        }
    }
}