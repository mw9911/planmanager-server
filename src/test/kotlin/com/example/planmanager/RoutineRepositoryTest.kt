package com.example.planmanager

import com.example.planmanager.routine.entity.RoutineEntity
import com.example.planmanager.routine.repository.RoutineRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers // 1. 클래스 레벨에서 Testcontainers 생명주기 관리 선언
class RoutineRepositoryTest {

    companion object {
        // 2. 사용할 MySQL Docker 이미지 및 초기 설정 명시
        @Container
        val mysqlContainer = MySQLContainer<Nothing>("mysql:8.4.9").apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_password")
        }

        // 3. Spring Boot의 DataSource 프로퍼티를 컨테이너의 정보로 동적 오버라이딩
        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", mysqlContainer::getUsername)
            registry.add("spring.datasource.password", mysqlContainer::getPassword)
            registry.add("spring.datasource.driver-class-name", mysqlContainer::getDriverClassName)
        }
    }

    @Test
    fun `MySQL 컨테이너 연동 테스트`() {
        // given - when - then 로직 작성
        // 이 테스트는 H2가 아닌, 위에서 띄운 실제 MySQL 8.4.9 위에서 실행됨
    }
    @Autowired
    private lateinit var routineRepository: RoutineRepository
    @Test
    fun `MySQL 컨테이너에서 루틴을 저장하고 조회할 수 있어야 한다`() {
        val routine = RoutineEntity(userId = 1L, title = "아침 조깅", intervalDays = 1, status = "ACTIVE")
        routineRepository.save(routine)

        val found = routineRepository.findAllByUserIdAndStatus(1L, "ACTIVE")

        assertEquals(1, found.size)
        assertEquals("아침 조깅", found[0].title)
    }
}