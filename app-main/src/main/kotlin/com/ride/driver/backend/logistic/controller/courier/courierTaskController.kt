package com.ride.driver.backend.logistic.controller

import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
@RequestMapping("api/v1/courier/task")
class CourierTaskController (
    private val logisticsService: LogisticsService
){
    private val logger: Logger = LoggerFactory.getLogger(CourierTaskController::class.java)

    @GetMapping("/poll")
    fun pollCurrentTask(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=courier_poll_task_request_received courierId={}", courierDetails.accountId)
        val assignedTask: TaskDataDTO? = logisticsService.pollTask(courierDetails)
        return ResponseEntity.ok(assignedTask)
    }

   @PutMapping("/accept")
    fun acceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=courier_accept_task_request_received courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        val updatedTask: TaskDataDTO = logisticsService.acceptAssignedToCourierTask(
            taskStatusActionDTO = taskStatusActionDTO,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }

   @PutMapping("/complete/pickup")
    fun completePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=courier_complete_pickup_request_received courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        val updatedTask: TaskDataDTO = logisticsService.completePickupTask(
            taskStatusActionDTO = taskStatusActionDTO,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }

    @PutMapping("/complete/dropoff")
    fun completeDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim        
    ): ResponseEntity<TaskDataDTO> {
        logger.info("event=courier_complete_dropoff_request_received courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        val updatedTask: TaskDataDTO = logisticsService.completeDropOffTask(
         taskStatusActionDTO = taskStatusActionDTO,
         courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedTask)
    }    
}