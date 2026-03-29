package com.ride.driver.backend.logistic.controller.courier

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.logistic.mapper.toTaskDataDTO
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.dto.TaskDataDTO

@RestController
@RequestMapping("/api/v1/logistics/consumer")
class CourierTaskController (
    private val logisticsService: LogisticsService
){
    @GetMapping("/task/poll")
    fun pollCurrentTask(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val assignedTask: Task? = logisticsService.pollForTask(
            courierId = courierDetails.accountId
        )
        return ResponseEntity.ok(assignedTask?.toTaskDataDTO())
    }

   @PutMapping("/task/accept")
    fun acceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsAccepted(
            courierId = courierDetails.accountId,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask.toTaskDataDTO())
    }

   @PutMapping("/task/complete/pickup")
    fun completePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsPickedUp(
            courierId = courierDetails.accountId,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask.toTaskDataDTO())
    }

    @PutMapping("/task/complete/dropoff")
    fun completeDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim        
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsDroppedOff(
            courierId = courierDetails.accountId,
            taskId = taskStatusActionDTO.taskId
        )
        return ResponseEntity.ok(updatedTask.toTaskDataDTO())
    }    
}