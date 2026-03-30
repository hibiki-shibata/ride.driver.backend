package com.ride.driver.backend.logistic.service

import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.model.OrderedItem
import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.dto.CreateTaskDTO
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.mapper.toTaskDataDTO
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim

@Service
class LogisticsService(
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val merchantMenuItemRepository: MerchantItemRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(LogisticsService::class.java)

    fun pollTask(courierDetails: AccessTokenClaim): TaskDataDTO? {
        val savedTask: Task = taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierDetails.accountId, 
                TaskStatus.READY_FOR_ASSIGNMENT
        ).firstOrNull() ?: return null
        return savedTask.toTaskDataDTO()
    }

    @Transactional
    fun createTask(
        createTaskDTO: CreateTaskDTO,
        consumerDetails: AccessTokenClaim
    ): TaskDataDTO {
        // validate if the item data
        val savedMerchantMenu: List<MerchantItem?> = merchantMenuItemRepository.findByMerchantProfile_Id(createTaskDTO.merchantID)
        val addedItemsData: List<MerchantItem?> = savedMerchantMenu.filter { createTaskDTO.addedItemIDs.contains(it?.id.toString()) }
        if (addedItemsData.size != createTaskDTO.addedItemIDs.size) throw Exception("One or more ordered items are invalid for the given merchant")        
        val createdTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerProfileRepository.findById(consumerDetails.accountId).orElseThrow { Exception("Consumer not found with ID: ${consumerDetails.accountId}")},
                merchantProfile = merchantProfileRepository.findById(createTaskDTO.merchantID).orElseThrow { Exception("Merchant not found with ID: ${createTaskDTO.merchantID}")},
                taskStatus = TaskStatus.CREATED,
                orderedItems = addedItemsData.map { item ->
                    OrderedItem(
                        itemId = item?.id ?: throw Exception("Item ID is null"),
                        name = item?.name ?: throw Exception("Item name is null"),
                        description = item?.description,
                        price = item?.price ?: throw Exception("Price not registered"),
                        merchantId = item?.merchantProfile?.id ?: throw Exception("Merchant profile ID is null")
                    )
                }
             )
        )
        logger.info("event=task_created taskId={} consumerId={} merchantId={}", createdTask.id, consumerDetails.accountId, createTaskDTO.merchantID)
        return createdTask.toTaskDataDTO()
    }

    @Transactional
    fun mxMarkAsReadyToDeliver(
        taskStatusActionDTO: TaskStatusActionDTO,
        merchantDetails: AccessTokenClaim
    ): TaskDataDTO {
        val savedTask: Task = taskRepository.findByIdAndTaskStatus(
            UUID.fromString(taskStatusActionDTO.taskId),
            TaskStatus.CREATED
        ).firstOrNull() ?: throw Exception("Task not found with ID: ${taskStatusActionDTO.taskId} in CREATED status")
        if (savedTask.merchantProfile.id != merchantDetails.accountId) throw Exception("Task does not belong to the merchant")
        savedTask.taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
        val updatedAssignedTask: Task = taskRepository.save(savedTask)
        logger.info("event=merchant_mark_task_ready_completed merchantId={} taskId={}", merchantDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }

    @Transactional
    fun cpAcceptTask(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierDetails.accountId,
                TaskStatus.READY_FOR_ASSIGNMENT
        ).firstOrNull() ?: throw Exception("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        if (assignedTask.id.toString() != taskStatusActionDTO.taskId) return throw Exception("Task ID does not match the assigned task for this courier")
        assignedTask.taskStatus = TaskStatus.IN_PICKUP
        val updatedAssignedTask: Task = taskRepository.save(assignedTask)
        logger.info("event=courier_accept_task_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }

    @Transactional
    fun completePickup(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierDetails.accountId,
                TaskStatus.IN_PICKUP
        ).firstOrNull() ?: throw Exception("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        if (assignedTask.id.toString() != taskStatusActionDTO.taskId) throw Exception("Task ID does not match the assigned task for this courier")
        assignedTask.taskStatus = TaskStatus.IN_DROPOFF
        val updatedAssignedTask: Task = taskRepository.save(assignedTask)
        logger.info("event=courier_complete_pickup_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }

    @Transactional
    fun completeDropOff(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByConsumerProfile_IdAndTaskStatus(
                courierDetails.accountId,
                TaskStatus.IN_DROPOFF
        ).firstOrNull() ?: throw Exception("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        if (assignedTask.id.toString() != taskStatusActionDTO.taskId) return throw Exception("Task ID does not match the assigned task for this courier")
        assignedTask.taskStatus = TaskStatus.DELIVERED
        val updatedAssignedTask: Task = taskRepository.save(assignedTask)
        logger.info("event=courier_complete_dropoff_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }
}