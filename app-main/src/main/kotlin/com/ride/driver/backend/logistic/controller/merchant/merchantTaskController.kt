package com.ride.driver.backend.logistic.merchant.controller

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.logistic.model.Task
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
        val updatedTask: Task = logisticsService.markTaskAsReadyForAssignment(
            merchantId = merchantDetails.accountId,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(
            TaskDataDTO(
                taskId = updatedTask.id.toString(),
                consumerName = updatedTask.consumerProfile?.name ?: "Unknown Consumer",
                consumerEmailaddress = updatedTask.consumerProfile?.emailAddress ?: "Unknown Phone Number",
                pickupAddress = updatedTask.merchantProfile?.merchantAddress ?: "Unknown Pickup Address",
                pickupLatitude = updatedTask.merchantProfile?.merchantAddressCoordinate?.latitude ?: 0.0,
                pickupLongitude = updatedTask.merchantProfile?.merchantAddressCoordinate?.longitude ?: 0.0,
                dropoffAddress = updatedTask.consumerProfile?.consumerAddress ?: "Unknown Dropoff Address",
                dropoffLatitude = updatedTask.consumerProfile?.consumerAddressCoordinate?.latitude ?: 0.0,
                dropoffLongitude = updatedTask.consumerProfile?.consumerAddressCoordinate?.longitude ?: 0.0,
                itemNames = updatedTask.orderedItems.map { it.name },
                totalPrice = updatedTask.totalPrice
            )
        )
    }  
}    