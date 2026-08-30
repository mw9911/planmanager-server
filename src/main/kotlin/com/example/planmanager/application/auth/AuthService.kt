package com.example.planmanager.application.auth

import com.example.planmanager.infrastructure.security.entity.RefreshTokenEntity
import com.example.planmanager.infrastructure.security.entity.RefreshTokenRepository
import com.example.planmanager.infrastructure.user.UserEntity
import com.example.planmanager.infrastructure.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository // 💡 누락되었던 의존성 주입 추가
) {

    @Transactional
    fun signUp(email: String, rawPassword: String) {
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("이미 가입된 이메일입니다.")
        }

        // 암호화된 비밀번호가 Null로 추론되지 않도록 엘비스 연산자(?:)를 통해 강제 String 타입 변환
        val encodedPassword = passwordEncoder.encode(rawPassword) ?: ""

        userRepository.save(UserEntity(email = email, passwordHash = encodedPassword))
    }
    // AuthService.kt의 login 함수 수정 예시
    @Transactional
    fun login(loginId: String, rawPassword: String): Pair<String, String> {
        // 💡 username 혹은 email 중 하나라도 일치하는 유저 조회 (UserRepository에 쿼리 메서드 추가 필요)
        val user = userRepository.findByUsernameOrEmail(loginId, loginId)
            ?: throw IllegalArgumentException("존재하지 않는 아이디 또는 이메일입니다.")

        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val accessToken = jwtProvider.createAccessToken(user.id, user.role)
        val refreshToken = jwtProvider.createRefreshToken(user.id, user.role)

        val tokenEntity = refreshTokenRepository.findById(user.id)
            .map { it.apply { this.token = refreshToken } }
            .orElse(RefreshTokenEntity(userId = user.id, token = refreshToken))
        refreshTokenRepository.save(tokenEntity)

        return Pair(accessToken, refreshToken)
    }
    @Transactional
    fun reissueToken(requestRefreshToken: String): Pair<String, String> {
        if (!jwtProvider.validateToken(requestRefreshToken)) {
            throw IllegalArgumentException("만료되었거나 유효하지 않은 Refresh Token입니다.")
        }

        val userId = jwtProvider.getUserIdFromToken(requestRefreshToken)
        val tokenEntity = refreshTokenRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("서버에 존재하지 않는 토큰입니다.") }

        if (tokenEntity.token != requestRefreshToken) {
            throw IllegalArgumentException("토큰 정보가 일치하지 않습니다. 다시 로그인해주세요.")
        }

        val user = userRepository.findById(userId).orElseThrow()

        // Token Rotation: 새 토큰 쌍 발급 및 DB 갱신
        val newAccessToken = jwtProvider.createAccessToken(userId, user.role)
        val newRefreshToken = jwtProvider.createRefreshToken(userId, user.role)

        tokenEntity.token = newRefreshToken
        return Pair(newAccessToken, newRefreshToken)
    }
}