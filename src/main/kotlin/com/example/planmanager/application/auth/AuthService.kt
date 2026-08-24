package com.example.planmanager.application.auth

import com.example.planmanager.infrastructure.user.UserEntity
import com.example.planmanager.infrastructure.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
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

    @Transactional(readOnly = true)
    fun login(email: String, rawPassword: String): String {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("존재하지 않는 이메일입니다.")

        // 평문 비밀번호와 암호화된 해시값 비교 검증
        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        // 검증 성공 시 JWT 토큰 발급
        return jwtProvider.createToken(user.id, user.role)
    }
}