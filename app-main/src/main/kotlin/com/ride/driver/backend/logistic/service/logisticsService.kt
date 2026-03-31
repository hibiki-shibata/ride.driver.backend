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
import com.ride.driver.backend.logistic.mapper.toOrderedItem
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.TaskNotFoundException
import com.ride.driver.backend.shared.exception.ItemNotFoundException
import com.ride.driver.backend.shared.exception.AccountNotFoundException

@Service
class LogisticsService(
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository,
    private val merchantItemRepository: MerchantItemRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(LogisticsService::class.java)

    fun pollTask(courierDetails: AccessTokenClaim): TaskDataDTO? {
        val savedTask: Task = taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierDetails.accountId, 
                TaskStatus.ASSIGNED_TO_COURIER
        ).firstOrNull() ?: return null
        return savedTask.toTaskDataDTO()
    }

    @Transactional
    fun createTask(
        createTaskDTO: CreateTaskDTO,
        consumerDetails: AccessTokenClaim
    ): TaskDataDTO {
        val consumerProfile: ConsumerProfile = consumerProfileRepository.findById(consumerDetails.accountId).orElseThrow { 
            AccountNotFoundException("Consumer not found with ID: ${consumerDetails.accountId}")
        }
        val merchantProfile: MerchantProfile = merchantProfileRepository.findById(createTaskDTO.merchantID).orElseThrow { 
            AccountNotFoundException("Merchant not found with ID: ${createTaskDTO.merchantID}")
        }
        val merchantItems: List<MerchantItem> = merchantItemRepository.findByIdInAndMerchantProfile_Id(
                createTaskDTO.orderedItemIDs.map { UUID.fromString(it) },
                createTaskDTO.merchantID
            )
        if (merchantItems.size != createTaskDTO.orderedItemIDs.size) throw ItemNotFoundException("One or more ordered items not found for the given merchant with ID: ${createTaskDTO.merchantID}")
        val orderedItems: List<OrderedItem> = merchantItems.map { it.toOrderedItem() }
        val createdTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerProfile,
                merchantProfile = merchantProfile,
                taskStatus = TaskStatus.CREATED,
                orderedItems = orderedItems,
            )
        )
        logger.info("event=task_created taskId={} consumerId={} merchantId={}", createdTask.id, consumerDetails.accountId, createTaskDTO.merchantID)
        return createdTask.toTaskDataDTO()
    }

    @Transactional
    fun readyForAssignment(
        taskStatusActionDTO: TaskStatusActionDTO,
        merchantDetails: AccessTokenClaim
    ): TaskDataDTO {
        val savedTask: Task = taskRepository.findByIdAndMerchantProfile_IdAndTaskStatus(
            UUID.fromString(taskStatusActionDTO.taskId),
            merchantDetails.accountId,
            TaskStatus.CREATED
        ) ?: throw TaskNotFoundException("Task not found with ID: ${taskStatusActionDTO.taskId} for the given merchant")        
        val updatedTask: Task = taskRepository.save(
            savedTask.apply { taskStatus = TaskStatus.READY_FOR_ASSIGNMENT }
        )
        logger.info("event=merchant_mark_task_ready_completed merchantId={} taskId={}", merchantDetails.accountId, taskStatusActionDTO.taskId)
        return updatedTask.toTaskDataDTO()
    }

    @Transactional
    fun acceptReadyForAssignmentTask(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                UUID.fromString(taskStatusActionDTO.taskId),
                courierDetails.accountId,
                TaskStatus.ASSIGNED_TO_COURIER
        ) ?: throw TaskNotFoundException("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        val updatedAssignedTask: Task = taskRepository.save(
            assignedTask.apply { taskStatus = TaskStatus.IN_PICKUP }
        )
        logger.info("event=courier_accept_task_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }

    @Transactional
    fun completePickupTask(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                UUID.fromString(taskStatusActionDTO.taskId),
                courierDetails.accountId,
                TaskStatus.IN_PICKUP
        )?: throw TaskNotFoundException("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        val updatedAssignedTask: Task = taskRepository.save(
            assignedTask.apply { taskStatus = TaskStatus.IN_DROPOFF }
        )
        logger.info("event=courier_complete_pickup_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }

    @Transactional
    fun completeDropOffTask(
        taskStatusActionDTO: TaskStatusActionDTO,
        courierDetails: AccessTokenClaim
    ): TaskDataDTO {
        val assignedTask: Task = taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                UUID.fromString(taskStatusActionDTO.taskId),
                courierDetails.accountId,
                TaskStatus.IN_DROPOFF
        ) ?: throw Exception("Task not found with ID: ${taskStatusActionDTO.taskId} for the given courier")
        val updatedAssignedTask: Task = taskRepository.save(
            assignedTask.apply { taskStatus = TaskStatus.DELIVERED }
        )
        logger.info("event=courier_complete_dropoff_completed courierId={} taskId={}", courierDetails.accountId, taskStatusActionDTO.taskId)
        return updatedAssignedTask.toTaskDataDTO()
    }
}