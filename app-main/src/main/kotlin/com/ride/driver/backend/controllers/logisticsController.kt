package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import com.ride.driver.backend.services.AccessTokenData
import com.ride.driver.backend.models.Coordinate
import java.util.UUID
import com.ride.driver.backend.repositories.CourierProfileRepository

data class TaskInfoDTO(
    val taskId: String,
    val earnings: Int,
    val venueName: String,
    val consumerName: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val description: String?,
)

@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val courierProfileRepository: CourierProfileRepository,
){
    @PostMapping("/location/update")
    fun updateLocation(@RequestBody location: Coordinate): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                currentLocation = location                
            ) ?: return ResponseEntity.status(404).body("Courier not found")
        )
        return ResponseEntity.ok("Location updated successfully")
    }

    @GetMapping("/poll/task")
    fun pollForTask(): ResponseEntity<TaskInfoDTO> {
        val sampleTask = TaskInfoDTO(
            taskId = "task123",
            earnings = 500,
            venueName = "Pizza Place",
            consumerName = "John Doe",
            pickupLocation = "123 Main St",
            dropoffLocation = "456 Elm St",
            description = "Deliver a large pepperoni pizza"
        )
        return ResponseEntity.ok(sampleTask)
    }

   @PostMapping("/task/accept")
    fun acceptTask(@RequestBody taskId: String): ResponseEntity<String> {
        println("Received task acceptance for task ID: $taskId")
        // Here you would typically update the task status in the database and perform any necessary business logic
        return ResponseEntity.ok("Task $taskId accepted successfully")
    }

   @PostMapping("/task/complete")
    fun completeTask(@RequestBody taskId: String): ResponseEntity<String> {
        println("Received task completion for task ID: $taskId")
        // Here you would typically update the task status in the database and perform any necessary business logic
        return ResponseEntity.ok("Task $taskId marked as completed")
    }
}