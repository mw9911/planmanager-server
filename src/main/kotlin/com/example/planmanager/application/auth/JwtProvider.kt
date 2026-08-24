package com.example.planmanager.application.auth

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
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
    private var expirationTime: Long = 0

    private lateinit var key: SecretKey

    /**
     * 의존성 주입 완료 후, Base64 인코딩된 시크릿 키를 암호화 알고리즘에 맞는 SecretKey 객체로 변환
     */
    @PostConstruct
    fun init() {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * 사용자 식별자(userId)와 권한(role)을 포함한 JWT 생성
     */
    fun createToken(userId: Long, role: String): String {
        val now = Date()
        val validity = Date(now.time + expirationTime)

        return Jwts.builder()
            .subject(userId.toString()) // 고유 식별자를 subject로 지정
            .claim("role", role)        // 권한 정보를 Custom Claim으로 추가
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)              // 최신 0.12.x 방식의 서명 메서드
            .compact()
    }

    /**
     * 토큰의 유효성 및 만료 여부 검증
     */
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key) // 파싱 시 서명 검증 키 전달
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            // 서명 오류, 만료, 손상된 토큰 등 검증 실패 시 false 반환
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * 토큰에서 userId(subject) 추출
     */
    fun getUserIdFromToken(token: String): Long {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        return claims.subject.toLong()
    }
}