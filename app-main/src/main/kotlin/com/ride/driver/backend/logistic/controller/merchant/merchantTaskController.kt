package com.ride.driver.backend.logistic.merchant.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.logistic.service.LogisticsService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantTaskController (
    private val logisticsService: LogisticsService
){
    @PostMapping("/task/ready")
    fun MxReadyTaskForAssignment(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenData
    ): ResponseEntity<Task> {
        val updatedTask: Task = logisticsService.markTaskAsReadyForAssignment(
            merchantId = merchantDetails.accountID,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask)
    }  
}    