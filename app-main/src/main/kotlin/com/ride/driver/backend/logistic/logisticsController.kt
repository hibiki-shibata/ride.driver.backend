package com.ride.driver.backend.logistic.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate
import java.util.UUID
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

data class TaskStatusActionDTO(
    @field:NotBlank
    val taskId: String
)

data class CreateTaskDTO(
    @field:NotBlank
    val merchantID: UUID,
)

@RestController
@RequestMapping("/api/v1/logistics")
class LogisticsController (
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val taskRepository: TaskRepository
){
    @GetMapping("/task/poll")
    fun pollForTask(
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<Task> {
        val courierId: UUID = courierDetails.accountID
        val assignedTask: Task? = taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.READY_FOR_ASSIGNMENT).firstOrNull()
        return if (assignedTask != null) ResponseEntity.ok(assignedTask) else ResponseEntity.status(204).build()        
    }

    @PostMapping("/task/create")
    fun createTask(
        @RequestBody createTaskDTO: CreateTaskDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenData        
    ): ResponseEntity<String> {
        val consumerId: UUID = consumerDetails.accountID
        taskRepository.save(
            Task(
                consumerProfile = consumerProfileRepository.findById(consumerId) ?: return ResponseEntity.status(404).body("Consumer not found"),
                merchantProfile = merchantProfileRepository.findById(createTaskDTO.merchantID) ?: return ResponseEntity.status(404).body("merchant not found"),                
                taskStatus = TaskStatus.CREATED
             )
        )
        return ResponseEntity.ok("Order created successfully")
    }

    @PostMapping("/task/ready")
    fun readyTaskForAssignment(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenData
    ): ResponseEntity<String> {
        val merchantId: UUID = merchantDetails.accountID
        val taskId: String = taskStatusActionDTO.taskId
        val taskToUpdate: Task = taskRepository.findById(UUID.fromString(taskId)) ?: throw Exception("Task not found with ID: $taskId")
        if (taskToUpdate.merchantProfile.id != merchantId) return ResponseEntity.status(403).body("This task does not belong to the merchant associated with the authenticated account")
        if (taskToUpdate.taskStatus != TaskStatus.CREATED) return ResponseEntity.status(400).body("Only tasks in CREATED status can be marked as READY_FOR_ASSIGNMENT")
        taskRepository.save(
            taskToUpdate.copy(
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )
        return ResponseEntity.ok("Task $taskId is now ready for assignment")
    }

   @PostMapping("/task/accept")
    fun acceptTask(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<String> {
        val taskId: String = taskStatusActionDTO.taskId
        val courierId: UUID = courierDetails.accountID
        val assignedTask: Task = taskRepository.findByCourierProfile_Id(courierId).firstOrNull() ?: return ResponseEntity.status(400).body("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return ResponseEntity.status(400).body("Task ID does not match the assigned task for this courier")
        taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_PICKUP
            )
        )
        return ResponseEntity.ok("Task $taskId accepted successfully")
    }

   @PostMapping("/task/complete/pickup")
    fun completePickup(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<String> {
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
    fun completeDropoff(
        @RequestBody @Valid taskStatusActionDTO: TaskStatusActionDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData        
    ): ResponseEntity<String> {
        val taskId: String = taskStatusActionDTO.taskId
            ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
        val assignedTask: Task = taskRepository.findByConsumerProfile_IdAndTaskStatus(courierId, TaskStatus.IN_DROPOFF).firstOrNull()
            ?: return ResponseEntity.status(400).body("No task assigned to this courier")
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