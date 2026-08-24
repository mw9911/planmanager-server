package com.example.planmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class PlanManagerServerApplication

fun main(args: Array<String>) {
    runApplication<PlanManagerServerApplication>(*args)
}
