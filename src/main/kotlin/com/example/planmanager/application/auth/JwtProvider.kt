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
    fun validateToken(token: String): Boolean { /* 기존 코드 유지 */ return true }
    fun getUserIdFromToken(token: String): Long { /* 기존 코드 유지 */ return 1L }
}