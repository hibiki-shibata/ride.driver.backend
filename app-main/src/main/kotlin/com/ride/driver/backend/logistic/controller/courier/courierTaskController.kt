package com.ride.driver.backend.logistic.controller.courier

import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.logistic.dto.TaskDataDTO

@RestController
@RequestMapping("/api/v1/logistics/consumer")
class CourierTaskController (
    private val logisticsService: LogisticsService
){
    @GetMapping("/task/poll")
    fun cpPollForTask(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val assignedTask: Task? = logisticsService.pollForTask(
            courierId = courierDetails.accountId
        )
        return ResponseEntity.ok(
            TaskDataDTO(
                taskId = assignedTask?.id.toString(),
                consumerName = assignedTask?.consumerProfile?.name ?: "Unknown Consumer",
                consumerEmailaddress = assignedTask?.consumerProfile?.emailAddress ?: "Unknown Phone Number",
                pickupAddress = assignedTask?.merchantProfile?.merchantAddress ?: "Unknown Pickup Address",
                pickupLatitude = assignedTask?.merchantProfile?.merchantAddressCoordinate?.latitude ?: 0.0,
                pickupLongitude = assignedTask?.merchantProfile?.merchantAddressCoordinate?.longitude ?: 0.0,
                dropoffAddress = assignedTask?.consumerProfile?.consumerAddress ?: "Unknown Dropoff Address",
                dropoffLatitude = assignedTask?.consumerProfile?.consumerAddressCoordinate?.latitude ?: 0.0,
                dropoffLongitude = assignedTask?.consumerProfile?.consumerAddressCoordinate?.longitude ?: 0.0,
                itemNames = assignedTask?.orderedItems?.map { it.name } ?: emptyList(),
                totalPrice = assignedTask?.totalPrice ?: 0.0
            )
        )
    }

   @PutMapping("/task/accept")
    fun cpAcceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsAccepted(
            courierId = courierDetails.accountId,
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

   @PutMapping("/task/complete/pickup")
    fun cpCompletePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsPickedUp(
            courierId = courierDetails.accountId,
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

    @PutMapping("/task/complete/dropoff")
    fun cpCompleteDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim        
    ): ResponseEntity<TaskDataDTO> {
        val updatedTask: Task = logisticsService.markTaskAsDroppedOff(
            courierId = courierDetails.accountId,
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