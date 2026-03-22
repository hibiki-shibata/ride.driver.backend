package com.ride.driver.backend.logistic.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.model.OrderedItem
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import java.util.UUID

@Service
class LogisticsService(
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val merchantMenuItemRepository: MerchantItemRepository,
    private val taskRepository: TaskRepository
){
    fun pollForTask(courierId: UUID): Task? {
        return taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.READY_FOR_ASSIGNMENT).firstOrNull()
    }

    fun createTask(consumerId: UUID, merchantId: UUID, orderedItemIDs: List<String>): Task {
        // validate if the item data
        val merchantMenuItemsAll: List<MerchantItem> = merchantMenuItemRepository.findByMerchantProfile_Id(merchantId)
        val orderedItemsDataRaw: List<MerchantItem> = merchantMenuItemsAll.filter { orderedItemIDs.contains(it.id.toString()) }
        if (orderedItemsDataRaw.size != orderedItemIDs.size) throw Exception("One or more ordered items are invalid for the given merchant")
        val createdTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerProfileRepository.findById(consumerId).orElseThrow { Exception("Consumer not found with ID: ${consumerId}") },
                merchantProfile = merchantProfileRepository.findById(merchantId) ?: return throw Exception("Merchant not found with ID: ${merchantId}"),
                taskStatus = TaskStatus.CREATED,
                orderedItems = orderedItemsDataRaw.map { item ->
                    OrderedItem(
                        itemId = item.id ?: throw Exception("Menu item ID is null"),
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        merchantId = item.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    )
                }
             )
        )
        return createdTask
    }

    fun markTaskAsReadyForAssignment(merchantId: UUID, taskId: String): Task {
        val taskToUpdate: Task = taskRepository.findById(UUID.fromString(taskId)) ?: throw Exception("Task not found with ID: $taskId")
        if (taskToUpdate.merchantProfile.id != merchantId) throw Exception("This task does not belong to the merchant associated with the authenticated account")
        if (taskToUpdate.taskStatus != TaskStatus.CREATED) throw Exception("Only tasks in CREATED status can be marked as READY_FOR_ASSIGNMENT")
        val updatedTask: Task = taskRepository.save(
            taskToUpdate.copy(
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )
        return updatedTask
    }

    fun markTaskAsReady(merchantId: UUID, taskId: String): Task {
        val taskToUpdate: Task = taskRepository.findByIdAndMerchantProfile_Id(UUID.fromString(taskId), merchantId) 
                ?: throw Exception("Task not found with ID: $taskId for the given merchant")
        if (taskToUpdate.taskStatus != TaskStatus.CREATED) return throw Exception("Only tasks in CREATED status can be marked as READY")
        val updatedTask: Task = taskRepository.save(
            taskToUpdate.copy(
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )
        return updatedTask
    }

    fun markTaskAsAccepted(courierId: UUID, taskId: String): Task {
        val assignedTask: Task = taskRepository.findByCourierProfile_Id(courierId).firstOrNull() ?: return throw Exception("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return throw Exception("Task ID does not match the assigned task for this courier")
        if (assignedTask.taskStatus != TaskStatus.READY_FOR_ASSIGNMENT) return throw Exception("Cannot accept task that is not in READY_FOR_ASSIGNMENT status")
        val updatedTask: Task = taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_PICKUP
            )
        )
        return updatedTask
    }

    fun markTaskAsPickedUp(courierId: UUID, taskId: String): Task {
        val assignedTask: Task = taskRepository.findByCourierProfile_Id(courierId).firstOrNull()
             ?: return throw Exception("No task assigned to this courier")
        if (assignedTask.id.toString() != taskId) return throw Exception("Task ID does not match the assigned task for this courier")
        if (assignedTask.taskStatus != TaskStatus.IN_PICKUP) return throw Exception("Cannot complete pickup for task that is not in IN_PICKUP status")
        val updatedTask: Task = taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.IN_DROPOFF
            )
        )
        return updatedTask
    }

    fun markTaskAsDroppedOff(courierId: UUID, taskId: String): Task {
        val assignedTask: Task = taskRepository.findByConsumerProfile_IdAndTaskStatus(courierId, TaskStatus.IN_DROPOFF).firstOrNull()
            ?: return throw Exception("This task is not assigned to this courier")
        if (assignedTask.id.toString() != taskId) return throw Exception("Task ID does not match the assigned task for this courier")
        if (assignedTask.taskStatus != TaskStatus.IN_DROPOFF) return throw Exception("Cannot complete dropoff for task that is not in IN_DROPOFF status")
        val updatedTask: Task = taskRepository.save(
            assignedTask.copy(
                taskStatus = TaskStatus.DELIVERED
            )
        )
        return updatedTask
    }
}