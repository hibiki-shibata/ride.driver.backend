package com.ride.driver.backend.logistic.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.merchant.models.MerchantItem
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.logistic.services.LogisticsService
import java.util.UUID
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotBlank

data class TaskStatusActionDTO(
    @field:NotBlank
    val taskId: String
)

data class CreateTaskDTO(
    @field:NotBlank
    val merchantID: UUID,

    @field:NotBlank
    @field:NotEmpty
    val orderedItemIDs: List<String>
)

@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val logisticsService: LogisticsService
){
    @GetMapping("/task/poll")
    fun cpPollForTask(
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<Task> {
        val assignedTask: Task? = logisticsService.pollForTask(
            courierId = courierDetails.accountID
        )
        return ResponseEntity.ok(assignedTask)
    }

    @PostMapping("/task/create")
    fun cxCreateTask(
        @RequestBody createTaskDTO: CreateTaskDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenData        
    ): ResponseEntity<Task> {
        val createdTask: Task = logisticsService.createTask(
            consumerId = consumerDetails.accountID,
            merchantId = createTaskDTO.merchantID,
            orderedItemIDs = createTaskDTO.orderedItemIDs
        )
        return ResponseEntity.ok(createdTask)
    }

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

   @PostMapping("/task/accept")
    fun cpAcceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<Task> {
        val updatedTask: Task = logisticsService.markTaskAsAccepted(
            courierId = courierDetails.accountID,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask)
    }

   @PostMapping("/task/complete/pickup")
    fun cpCompletePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<Task> {
        val updatedTask: Task = logisticsService.markTaskAsPickedUp(
            courierId = courierDetails.accountID,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask)
    }

    @PostMapping("/task/complete/dropoff")
    fun cpCompleteDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData        
    ): ResponseEntity<Task> {
        val updatedTask: Task = logisticsService.markTaskAsDroppedOff(
            courierId = courierDetails.accountID,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask)
    }    
}