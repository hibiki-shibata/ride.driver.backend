package com.ride.driver.backend.logistic.controller.courier

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.service.LogisticsService

@RestController
@RequestMapping("/api/v1/logistics/consumer")
class CourierTaskController (
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