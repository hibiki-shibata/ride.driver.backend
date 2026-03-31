package com.ride.driver.backend.merchant.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.Valid
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.dto.TaskDataDTO

@RestController
@RequestMapping("api/v1/merchants/task")
class MerchantTaskController (
    private val logisticsService: LogisticsService
){
    private val logger: Logger = LoggerFactory.getLogger(MerchantTaskController::class.java)

    @PutMapping("/ready")
    fun MxReadyTaskForAssignment(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=merchant_ready_task_request_received merchantId={} taskId={}", merchantDetails.accountId, taskStatusActionDTO.taskId)
        val updatedTask: TaskDataDTO = logisticsService.readyForAssignment(
            taskStatusActionDTO = taskStatusActionDTO,
            merchantDetails = merchantDetails
        )
        return ResponseEntity.ok(updatedTask)
    }  
}    