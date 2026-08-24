package com.example.planmanager.presentation.api.auth

import com.example.planmanager.application.auth.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AuthControllerTest {

    private lateinit var mockMvc: MockMvc
    private val authService = mockk<AuthService>()
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        // Security 필터를 배제하고 AuthController 단독 셋업
        mockMvc = MockMvcBuilders.standaloneSetup(AuthController(authService)).build()
    }

    @Test
    @DisplayName("TDD: 회원가입 API 호출 시 200 OK와 성공 메시지를 반환한다")
    fun signUpApiSuccess() {
        // given
        val request = SignUpRequest("api@test.com", "1234")
        every { authService.signUp(any(), any()) } returns Unit

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.token").isEmpty)
    }

    @Test
    @DisplayName("TDD: 로그인 API 호출 시 200 OK와 JWT 토큰을 반환한다")
    fun loginApiSuccess() {
        // given
        val request = LoginRequest("api@test.com", "1234")
        every { authService.login(any(), any()) } returns "test_jwt_token"

        // when & then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("로그인 성공"))
            .andExpect(jsonPath("$.token").value("test_jwt_token"))
    }
}