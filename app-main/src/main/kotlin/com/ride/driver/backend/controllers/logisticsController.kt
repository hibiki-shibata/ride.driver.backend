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
import jakarta.validation.Valid


data class StatusUpdateDTO(
    val isOnline: Boolean,
)

data class TaskActionDTO(
    val taskId: String
)

@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    @PostMapping("/update/mylocation")
    fun updateLocation(@RequestBody @Valid location: Coordinate): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
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
        val courierId: UUID = courierDetails.additonalClaims.accountID
        val assignedTask: Task? = taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.READY_FOR_ASSIGNMENT).firstOrNull()
        return if (assignedTask != null) ResponseEntity.ok(assignedTask) else ResponseEntity.status(204).build()        
    }

    @PostMapping("/update/status")
    fun updateStatus(@RequestBody @Valid statusUpdateDTO: StatusUpdateDTO): ResponseEntity<String> {
        val isOnline: Boolean = statusUpdateDTO.isOnline
        // val isOnline: Boolean = statusUpdateDTO.isOnline
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
        courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                cpStatus = if (isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
            ) ?: return ResponseEntity.status(404).body("Courier not found")
        )
        return ResponseEntity.ok("Status updated successfully")
    }

   @PostMapping("/task/accept")
    fun acceptTask(@RequestBody @Valid taskActionDTO: TaskActionDTO): ResponseEntity<String> {
        val taskId: String = taskActionDTO.taskId
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
        val assignedTask: Task = taskRepository.findByCourierProfile_Id(courierId).firstOrNull() ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_PICKUP
            )
        )
        return ResponseEntity.ok("Task $taskId accepted successfully")
    }

   @PostMapping("/task/complete/pickup")
    fun completePickup(@RequestBody @Valid taskActionDTO: TaskActionDTO): ResponseEntity<String> {
        val taskId: String = taskActionDTO.taskId
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
        val assignedTask: Task = taskRepository.findByCourierProfile_Id(courierId).firstOrNull()
             ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        if (assignedTask.taskStatus != TaskStatus.IN_PICKUP) return ResponseEntity.status(400).body("Cannot complete pickup for task that is not in PICKUP status")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_DROPOFF
            )
        )
        return ResponseEntity.ok("Pickup for task $taskId completed successfully")
    }

    @PostMapping("/task/complete/dropoff")
    fun completeDropoff(@RequestBody @Valid taskActionDTO: TaskActionDTO): ResponseEntity<String> {
        val taskId: String = taskActionDTO.taskId
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData 
            ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
        val assignedTask: Task = taskRepository.findByConsumerProfile_IdAndTaskStatus(courierId, TaskStatus.IN_DROPOFF).firstOrNull()
            ?: return ResponseEntity.status(404).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        if (assignedTask.taskStatus != TaskStatus.IN_DROPOFF) return ResponseEntity.status(400).body("Cannot complete dropoff for task that is not in DROPOFF status")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.DELIVERED
            )
        )
        return ResponseEntity.ok("Dropoff for task $taskId completed successfully")
    }    
}