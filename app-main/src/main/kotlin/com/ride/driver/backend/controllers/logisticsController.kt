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
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.logistics.TaskStatus
import java.util.UUID
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.TaskRepository



@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    @PostMapping("/update/mylocation")
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
    fun pollForTask(): ResponseEntity<Task> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        val assignedTask: Task = taskRepository.findByAssignedCourierId(courierId) ?: return ResponseEntity.status(404).body(null)
        return ResponseEntity.ok(assignedTask)
    }

    @PostMapping("/update/status")
    fun updateStatus(@RequestBody isOnline: Boolean): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                cpStatus = if (isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
            ) ?: return ResponseEntity.status(404).body("Courier not found")
        )
        return ResponseEntity.ok("Status updated successfully")
    }

    @PostMapping("/task/reject")
    fun rejectTask(@RequestBody taskId: String): ResponseEntity<String> {
        println("Received task rejection for task ID: $taskId")
        // 
        return ResponseEntity.ok("Task $taskId rejected successfully")
    }

   @PostMapping("/task/accept")
    fun acceptTask(@RequestBody taskId: String): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        val assignedTask: Task = taskRepository.findByAssignedCourierId(courierId) ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_PICKUP
            )
        )
        return ResponseEntity.ok("Task $taskId accepted successfully")
    }

   @PostMapping("/task/complete/pickup")
    fun completePickup(@RequestBody taskId: String): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        val assignedTask: Task = taskRepository.findByAssignedCourierId(courierId) ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_DROPOFF
            )
        )
        return ResponseEntity.ok("Pickup for task $taskId completed successfully")
    }

    @PostMapping("/task/complete/dropoff")
    fun completeDropoff(@RequestBody taskId: String): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.courierId
        val assignedTask: Task = taskRepository.findByAssignedCourierId(courierId) ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.DELIVERED
            )
        )
        return ResponseEntity.ok("Dropoff for task $taskId completed successfully")
    }
}