package com.example.planmanager.application.auth

import com.example.planmanager.infrastructure.user.UserEntity
import com.example.planmanager.infrastructure.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtProvider = mockk<JwtProvider>()
    private val authService = AuthService(userRepository, passwordEncoder, jwtProvider)

    @Test
    @DisplayName("TDD: 정상적인 이메일과 비밀번호로 회원가입에 성공해야 한다")
    fun signUpSuccess() {
        // given
        val email = "test@test.com"
        val rawPassword = "password123"
        every { userRepository.findByEmail(email) } returns null
        every { passwordEncoder.encode(rawPassword) } returns "encoded_password"
        every { userRepository.save(any()) } returns UserEntity(email = email, passwordHash = "encoded_password")

        // when
        authService.signUp(email, rawPassword)

        // then
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("TDD: 정상적인 정보로 로그인 시 JWT 토큰을 반환해야 한다")
    fun loginSuccess() {
        // given
        val email = "test@test.com"
        val rawPassword = "password123"
        val mockUser = UserEntity(id = 1L, email = email, passwordHash = "encoded_password", role = "ROLE_USER")

        every { userRepository.findByEmail(email) } returns mockUser
        every { passwordEncoder.matches(rawPassword, "encoded_password") } returns true
        every { jwtProvider.createToken(1L, "ROLE_USER") } returns "mock_jwt_token"

        // when
        val token = authService.login(email, rawPassword)

        // then
        assertEquals("mock_jwt_token", token)
    }

    @Test
    @DisplayName("TDD: 비밀번호가 틀리면 예외를 발생시켜야 한다")
    fun loginFailWhenPasswordWrong() {
        // given
        val email = "test@test.com"
        val mockUser = UserEntity(email = email, passwordHash = "encoded_password")

        every { userRepository.findByEmail(email) } returns mockUser
        every { passwordEncoder.matches("wrong_password", "encoded_password") } returns false

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            authService.login(email, "wrong_password")
        }
        assertEquals("비밀번호가 일치하지 않습니다.", exception.message)
    }
}