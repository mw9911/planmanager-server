package com.example.planmanager.application.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils

class JwtProviderTest {

    // 컴파일 에러(Unresolved reference)가 발생하는 것이 TDD의 정상적인 첫 단계입니다.
    private lateinit var jwtProvider: JwtProvider

    @BeforeEach
    fun setUp() {
        jwtProvider = JwtProvider()
        // @Value 로 주입될 설정값을 Reflection을 통해 강제 세팅 (단위 테스트 목적)
        ReflectionTestUtils.setField(
            jwtProvider,
            "secretKey",
            "VmVyeVNlY3JldEtleUZvclBsYW5NYW5hZ2VyQXBwbGljYXRpb25BdXRoZW50aWNhdGlvblB1cnBvc2U="
        )
        ReflectionTestUtils.setField(jwtProvider, "expirationTime", 3600000L) // 1시간
        jwtProvider.init() // 생성 후 초기화 메서드 호출
    }

    @Test
    @DisplayName("TDD: 유저 ID와 권한으로 JWT를 발급하고, 해당 토큰이 유효한지 파싱할 수 있어야 한다")
    fun generateAndValidateToken() {
        // given
        val userId = 100L
        val role = "ROLE_USER"

        // when
        val token = jwtProvider.createToken(userId, role)

        // then
        assertTrue(jwtProvider.validateToken(token))
        assertEquals(userId, jwtProvider.getUserIdFromToken(token))
    }
}