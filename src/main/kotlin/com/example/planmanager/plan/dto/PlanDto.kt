package com.example.planmanager.plan.dto

import com.example.planmanager.plan.entity.PlanEntity
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class PlanCreateRequest(
    val title: String,

    // 💡 클라이언트의 String("YYYY-MM-DD")을 LocalDate로 정확히 파싱하도록 규격 강제
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val planDate: LocalDate
)

data class PlanResponse(
    val id: Long,
    val title: String,

    // 💡 서버의 LocalDate를 클라이언트가 인식할 수 있는 String으로 정확히 직렬화
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val planDate: LocalDate,
    val isCompleted: Boolean
) {
    // PlanService에서 PlanResponse(it) 호출 시 사용되는 부생성자
    constructor(entity: PlanEntity) : this(
        id = entity.id,
        title = entity.title,
        planDate = entity.planDate,
        isCompleted = entity.isCompleted
    )
}
data class PlanSyncRequest(
    val title: String,

    // 💡 단일 생성과 동일하게 직렬화 규격 강제
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val planDate: LocalDate,

    val isCompleted: Boolean
)