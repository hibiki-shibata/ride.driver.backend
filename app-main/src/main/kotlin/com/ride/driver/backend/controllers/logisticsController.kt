package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import com.ride.driver.backend.models.Coordinate
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.logistics.TaskStatus
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.repositories.ConsumerProfileRepository
import com.ride.driver.backend.repositories.VenueProfileRepository
import com.ride.driver.backend.services.AccessTokenData
import java.util.UUID
import jakarta.validation.Valid

data class TaskStatusActionDTO(
    val taskId: String
)

data class CreateTaskDTO(
    val venueID: UUID,
    val pickupLocation: Coordinate,
    val dropoffLocation: Coordinate
)

@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val venueProfileRepository: VenueProfileRepository,
    private val taskRepository: TaskRepository
){
    @GetMapping("/poll/task")
    fun pollForTask(): ResponseEntity<Task> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
        val assignedTask: Task? = taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.READY_FOR_ASSIGNMENT).firstOrNull()
        return if (assignedTask != null) ResponseEntity.ok(assignedTask) else ResponseEntity.status(204).build()        
    }

    @PostMapping("/task/create")
    fun createTask(@RequestBody createTaskDTO: CreateTaskDTO): ResponseEntity<String> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.accountID
        taskRepository.save(
            Task(
                consumerProfile = consumerProfileRepository.findById(consumerId) ?: return ResponseEntity.status(404).body("Consumer not found"),
                venueProfile = venueProfileRepository.findById(createTaskDTO.venueID) ?: return ResponseEntity.status(404).body("Venue not found"),                
                taskStatus = TaskStatus.CREATED
             )
        )
        return ResponseEntity.ok("Order created successfully")
    }

    @PostMapping("/task/ready")
    fun readyTaskForAssignment(@RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO): ResponseEntity<String> {
        val venueDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val venueId: UUID = venueDetails.accountID
        val taskId: String = taskStatusActionDTO.taskId
        val taskToUpdate: Task = taskRepository.findById(UUID.fromString(taskId)) ?: throw Exception("Task not found with ID: $taskId")
        if (taskToUpdate.venueProfile.id != venueId) return ResponseEntity.status(403).body("This task does not belong to the venue associated with the authenticated account")
        if (taskToUpdate.taskStatus != TaskStatus.CREATED) return ResponseEntity.status(400).body("Only tasks in CREATED status can be marked as READY_FOR_ASSIGNMENT")
        taskRepository.save(
            taskToUpdate.copy(
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )
        return ResponseEntity.ok("Task $taskId is now ready for assignment")
    }

   @PostMapping("/task/accept")
    fun acceptTask(@RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val taskId: String = taskStatusActionDTO.taskId
        val courierId: UUID = courierDetails.accountID
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
    fun completePickup(@RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val taskId: String = taskStatusActionDTO.taskId
        val courierId: UUID = courierDetails.accountID
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
    fun completeDropoff(@RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO): ResponseEntity<String> {
        val taskId: String = taskStatusActionDTO.taskId
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData 
            ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
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