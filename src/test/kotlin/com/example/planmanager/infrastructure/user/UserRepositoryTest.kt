package com.example.planmanager.infrastructure.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    @DisplayName("DB: 새로운 유저를 저장하고 이메일로 조회할 수 있어야 한다")
    fun saveAndFindByEmail() {
        // given
        val user = UserEntity(
            email = "developer@test.com",
            passwordHash = "hashed_password_123",
            role = "ROLE_USER"
        )
        userRepository.save(user)

        // when
        val foundUser = userRepository.findByEmail("developer@test.com")

        // then
        assertNotNull(foundUser)
        assertEquals("hashed_password_123", foundUser?.passwordHash)
        assertEquals("ROLE_USER", foundUser?.role)
    }
}