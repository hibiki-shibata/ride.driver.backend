package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity

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
class LogisticsController (){
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
}