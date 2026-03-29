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
import com.ride.driver.backend.logistic.service.LogisticsService
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
        val assignedTask: TaskDataDTOs = logisticsService.pollForTask(courierDetails)
        return ResponseEntity.ok(assignedTask)
    }

   @PutMapping("/task/accept")
    fun acceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: TaskDataDTOs = logisticsService.markTaskAsAccepted(
            taskStatusActionDTO = taskStatusActionDTO,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }

   @PutMapping("/task/complete/pickup")
    fun completePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: TaskDataDTOs = logisticsService.markTaskAsPickedUp(
            taskStatusActionDTO = taskStatusActionDTO,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }

    @PutMapping("/task/complete/dropoff")
    fun completeDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim        
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: TaskDataDTOs = logisticsService.markTaskAsDroppedOff(
         taskStatusActionDTO = taskStatusActionDTO,
         courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }    
}