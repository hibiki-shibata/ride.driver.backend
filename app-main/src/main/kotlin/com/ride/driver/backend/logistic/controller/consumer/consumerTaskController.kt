package com.ride.driver.backend.logistic.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.logistic.dto.CreateTaskDTO

@RestController
@RequestMapping("api/v1/consumer/task")
class ConsumerTaskController (
    private val logisticsService: LogisticsService
){
    private val logger:  Logger = LoggerFactory.getLogger(ConsumerTaskController::class.java)

    @PostMapping("/create")
    fun createTask(
        @RequestBody createTaskDTO: CreateTaskDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim        
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=consumer_create_task_request_received consumerId={}", consumerDetails.accountId)
        val createdTask: TaskDataDTO = logisticsService.createTask(
            createTaskDTO = createTaskDTO,
            consumerDetails = consumerDetails
        )
        return ResponseEntity.ok(createdTask)
    }
}