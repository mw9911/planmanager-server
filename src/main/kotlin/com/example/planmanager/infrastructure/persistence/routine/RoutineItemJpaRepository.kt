package com.example.planmanager.infrastructure.persistence.routine

import org.springframework.data.jpa.repository.JpaRepository

interface RoutineItemJpaRepository : JpaRepository<RoutineItemEntity, Long>