package com.example.planmanager.service.plan
import com.example.planmanager.plan.dto.PlanCreateRequest
import com.example.planmanager.plan.entity.PlanEntity
import com.example.planmanager.plan.repository.PlanRepository
import com.example.planmanager.plan.service.PlanService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class PlanServiceTest {

    @MockK
    lateinit var planRepository: PlanRepository // 의존성 모킹 (가짜 객체)

    @InjectMockKs
    lateinit var planService: PlanService // 테스트 대상 객체

    @Test
    fun `계획 생성 시 Repository의 save가 1회 호출되고 Response를 반환한다`() {
        // [1] Arrange (준비)
        val userId = "3"
        val request = PlanCreateRequest(title = "TDD 테스트", planDate = LocalDate.parse("2026-08-23"))
        val mockEntity = PlanEntity(userId = userId.toLong(), title = request.title, planDate = request.planDate)

        // Mocking: save 호출 시 가짜 엔티티 반환 설정
        every { planRepository.save(any()) } returns mockEntity

        // [2] Act (실행)
        val result = planService.createPlan(userId.toLong(), request)

        // [3] Assert (검증)
        assertEquals("TDD 테스트", result.title)
        assertEquals(LocalDate.parse("2026-08-23"), result.planDate)
        verify(exactly = 1) { planRepository.save(any()) } // 횟수 검증
    }
}