package com.ride.driver.backend.logistic.merchant.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.dto.TaskDataDTO

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantTaskController (
    private val logisticsService: LogisticsService
){
    @PutMapping("/task/ready")
    fun MxReadyTaskForAssignment(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: TaskDataDTO = logisticsService.mxMarkAsReadyToDeliver(
            taskStatusActionDTO = taskStatusActionDTO,
            merchantDetails = merchantDetails
        )
        return ResponseEntity.ok(updatedTask)
    }  
}    