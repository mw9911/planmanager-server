// [수정된 JwtProvider.kt]
package com.example.planmanager.application.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider {
    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private var accessTokenExpiration: Long = 0 // 기존 만료시간 (예: 30분)

    @Value("\${jwt.refresh-expiration}")
    private var refreshTokenExpiration: Long = 0 // 신규 만료시간 (예: 14일)

    private lateinit var key: SecretKey

    @PostConstruct
    fun init() {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createAccessToken(userId: Long, role: String): String {
        return buildToken(userId, role, accessTokenExpiration)
    }

    fun createRefreshToken(userId: Long, role: String): String {
        return buildToken(userId, role, refreshTokenExpiration)
    }

    private fun buildToken(userId: Long, role: String, expiration: Long): String {
        val now = Date()
        val validity = Date(now.time + expiration)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", role)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    // validateToken, getUserIdFromToken 로직은 기존 코드[cite: 10]와 동일하게 유지
    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parser()
            .verifyWith(key) // 💡 서명 검증을 위한 Key 삽입
            .build()
            .parseSignedClaims(token)
            .payload

        return claims.subject.toLong() // 💡 하드코딩 1L을 제거하고 실제 토큰에 담긴 값을 추출
    }

    // 토큰 유효성 검증 로직 정상화 (만료 여부, 변조 여부 체크)
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false // 만료되거나 손상된 토큰일 경우 false 반환
        }
    }
}