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
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Transactional
    fun signUp(username: String, email: String, rawPassword: String) { // 💡 username 매개변수 추가
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("이미 가입된 이메일입니다.")
        }

        // (선택) username 중복 검증 로직이 필요하다면 여기에 추가 가능

        val encodedPassword = passwordEncoder.encode(rawPassword) ?: ""
        // 💡 엔티티 생성 시 username 필드 맵핑
        userRepository.save(UserEntity(username = username, email = email, passwordHash = encodedPassword))
    }

    @Transactional
    fun login(loginId: String, rawPassword: String): Pair<String, String> { // 💡 매개변수 loginId로 변경
        // 💡 아이디 또는 이메일 일치 여부 확인
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

        val newAccessToken = jwtProvider.createAccessToken(userId, user.role)
        val newRefreshToken = jwtProvider.createRefreshToken(userId, user.role)

        tokenEntity.token = newRefreshToken
        return Pair(newAccessToken, newRefreshToken)
    }
}