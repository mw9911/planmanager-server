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
            authService.signUp(request.email, request.password)
            ResponseEntity.ok(AuthResponse(accessToken = null, refreshToken = null, message = "회원가입이 완료되었습니다."))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(accessToken = null, refreshToken = null, message = e.message ?: "회원가입 실패"))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val (accessToken, refreshToken) = authService.login(request.email, request.password)

            // 💡 1. 웹 브라우저를 위한 HttpOnly 쿠키 생성 (XSS 방어 및 Cross-Origin 허용)
            val cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // AWS 환경 등 HTTPS 통신에서만 전송 허용
                .path("/")
                .sameSite("None") // 프론트와 백엔드 도메인이 다를 때 필수
                .maxAge(14 * 24 * 60 * 60) // 14일 (초 단위 설정)
                .build()

            // 💡 2. 안드로이드 앱을 위해 Body에도 토큰을 그대로 유지 (하위 호환성)
            ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse(accessToken, refreshToken, "로그인 성공"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(AuthResponse(null, null, e.message ?: "로그인 실패"))
        }
    }

    @PostMapping("/reissue")
    fun reissue(
        // 💡 3. 앱(Header) 또는 웹 브라우저(Cookie) 양쪽에서 들어오는 토큰을 모두 대응
        @RequestHeader("Refresh-Token", required = false) headerToken: String?,
        @CookieValue(name = "refreshToken", required = false) cookieToken: String?
    ): ResponseEntity<AuthResponse> {

        val refreshToken = headerToken ?: cookieToken

        if (refreshToken.isNullOrBlank()) {
            return ResponseEntity.status(401).body(AuthResponse(null, null, "Refresh Token이 존재하지 않습니다."))
        }

        return try {
            val (newAccessToken, newRefreshToken) = authService.reissueToken(refreshToken)

            // 💡 4. 재발급 시에도 웹을 위한 새 쿠키 세팅
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