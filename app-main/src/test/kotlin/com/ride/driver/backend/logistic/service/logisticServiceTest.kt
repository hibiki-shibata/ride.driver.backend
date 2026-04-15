package com.ride.driver.backend.logistic.service

import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.logistic.dto.CreateTaskDTO
import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.dto.CartItem
import com.ride.driver.backend.logistic.mapper.toOrderedItem
import com.ride.driver.backend.logistic.mapper.toTaskDataDTO
import com.ride.driver.backend.logistic.model.OrderedItem
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.ItemNotFoundException
import com.ride.driver.backend.shared.exception.TaskNotFoundException
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Optional
import java.util.UUID
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class LogisticsServiceTest {

    private lateinit var consumerProfileRepository: ConsumerProfileRepository
    private lateinit var merchantProfileRepository: MerchantProfileRepository
    private lateinit var merchantItemRepository: MerchantItemRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var logisticsService: LogisticsService

    @BeforeEach
    fun setUp() {
        consumerProfileRepository = mockk()
        merchantProfileRepository = mockk()
        merchantItemRepository = mockk()
        taskRepository = mockk()

        logisticsService = LogisticsService(
            consumerProfileRepository = consumerProfileRepository,
            merchantProfileRepository = merchantProfileRepository,
            merchantItemRepository = merchantItemRepository,
            taskRepository = taskRepository
        )

        mockkStatic(Task::toTaskDataDTO)
        mockkStatic(MerchantItem::toOrderedItem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `pollTask should return null when no assigned task exists`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockk<AccessTokenClaim>()
        val emptyPage: Page<Task> = PageImpl<Task>(emptyList())

        every { courierDetails.accountId } returns courierId
        every {
            taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER,
                PageRequest.of(0, 1)
            )
        } returns emptyPage

        val result = logisticsService.pollTask(courierDetails)

        assertNull(result)
        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER,
                PageRequest.of(0, 1)
            )
        }
        confirmVerified(courierDetails, taskRepository)
    }

    @Test
    fun `pollTask should return mapped task when assigned task exists`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockk<AccessTokenClaim>()
        val task = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val taskPage: Page<Task> = PageImpl<Task>(listOf(task))

        every { courierDetails.accountId } returns courierId
        every {
            taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER,
                PageRequest.of(0, 1)
            )
        } returns taskPage
        every { task.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.pollTask(courierDetails)

        assertSame(taskDataDTO, result)
        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByCourierProfile_IdAndTaskStatus(
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER,
                PageRequest.of(0, 1)
            )
        }
        verify(exactly = 1) { task.toTaskDataDTO() }
        confirmVerified(courierDetails, taskRepository, task, taskDataDTO)
    }

    @Test
    fun `createTask should create task and return mapped response`() {
        val consumerId = UUID.randomUUID()
        val merchantId = UUID.randomUUID()
        val itemId1 = UUID.randomUUID()
        val itemId2 = UUID.randomUUID()
        val savedTaskId = UUID.randomUUID()

        val consumerDetails = mockk<AccessTokenClaim>()
        val createTaskDTO = mockk<CreateTaskDTO>()
        val consumerProfile = mockk<ConsumerProfile>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantItem1 = mockk<MerchantItem>()
        val merchantItem2 = mockk<MerchantItem>()
        val orderedItem1 = mockk<OrderedItem>()
        val orderedItem2 = mockk<OrderedItem>()
        val savedTask = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val taskSlot = slot<Task>()

        every { consumerDetails.accountId } returns consumerId
        every { createTaskDTO.merchantId } returns merchantId
        every { createTaskDTO.selectedItems } returns listOf(
            CartItem(itemId1.toString(), "Item 1", 10.0, 2),
            CartItem(itemId2.toString(), "Item 2", 20.0, 3)
        )

        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(consumerProfile)
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every {
            merchantItemRepository.findByIdInAndMerchantProfile_Id(
                listOf(itemId1, itemId2),
                merchantId
            )
        } returns listOf(merchantItem1, merchantItem2)

        every { merchantItem1.toOrderedItem() } returns orderedItem1
        every { merchantItem2.toOrderedItem() } returns orderedItem2
        every { taskRepository.save(capture(taskSlot)) } returns savedTask
        every { savedTask.id } returns savedTaskId
        every { savedTask.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.createTask(createTaskDTO, consumerDetails)

        assertSame(taskDataDTO, result)
        assertSame(consumerProfile, taskSlot.captured.consumerProfile)
        assertSame(merchantProfile, taskSlot.captured.merchantProfile)
        assertEquals(TaskStatus.CREATED, taskSlot.captured.taskStatus)
        assertEquals(listOf(orderedItem1, orderedItem2), taskSlot.captured.orderedItems)

        verify(atLeast = 1) { consumerDetails.accountId }
        verify(atLeast = 1) { createTaskDTO.merchantId }
        verify(atLeast = 1) { createTaskDTO.selectedItems }
        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) {
            merchantItemRepository.findByIdInAndMerchantProfile_Id(
                listOf(itemId1, itemId2),
                merchantId
            )
        }
        verify(exactly = 1) { merchantItem1.toOrderedItem() }
        verify(exactly = 1) { merchantItem2.toOrderedItem() }
        verify(exactly = 1) { taskRepository.save(any()) }
        verify(exactly = 1) { savedTask.id }
        verify(exactly = 1) { savedTask.toTaskDataDTO() }
    }
    
    @Test
    fun `createTask should throw AccountNotFoundException when consumer does not exist`() {
        val consumerId = UUID.randomUUID()
        val consumerDetails = mockk<AccessTokenClaim>()
        val createTaskDTO = mockk<CreateTaskDTO>()

        every { consumerDetails.accountId } returns consumerId
        every { consumerProfileRepository.findById(consumerId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            logisticsService.createTask(createTaskDTO, consumerDetails)
        }

        assertEquals("Consumer not found with ID: $consumerId", exception.message)
    }

    @Test
    fun `createTask should throw AccountNotFoundException when merchant does not exist`() {
        val consumerId = UUID.randomUUID()
        val merchantId = UUID.randomUUID()

        val consumerDetails = mockk<AccessTokenClaim>()
        val createTaskDTO = mockk<CreateTaskDTO>()
        val consumerProfile = mockk<ConsumerProfile>()

        every { consumerDetails.accountId } returns consumerId
        every { createTaskDTO.merchantId } returns merchantId
        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(consumerProfile)
        every { merchantProfileRepository.findById(merchantId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            logisticsService.createTask(createTaskDTO, consumerDetails)
        }

        assertEquals("Merchant not found with ID: $merchantId", exception.message)
        verify(exactly = 1) { consumerDetails.accountId }
        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(atLeast = 1) { createTaskDTO.merchantId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        confirmVerified(
            consumerDetails,
            createTaskDTO,
            consumerProfileRepository,
            merchantProfileRepository,
            consumerProfile
        )
    }

    @Test
    fun `createTask should throw ItemNotFoundException when one or more items do not exist`() {
        val consumerId = UUID.randomUUID()
        val merchantId = UUID.randomUUID()
        val itemId1 = UUID.randomUUID()
        val itemId2 = UUID.randomUUID()

        val consumerDetails = mockk<AccessTokenClaim>()
        val createTaskDTO = mockk<CreateTaskDTO>()
        val consumerProfile = mockk<ConsumerProfile>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantItem1 = mockk<MerchantItem>()

        every { consumerDetails.accountId } returns consumerId
        every { createTaskDTO.merchantId } returns merchantId
        every { createTaskDTO.selectedItems } returns listOf(
            CartItem(itemId1.toString(), "Item 1", 10.0, 2),
            CartItem(itemId2.toString(), "Item 2", 20.0, 3)
        )

        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(consumerProfile)
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every {
            merchantItemRepository.findByIdInAndMerchantProfile_Id(
                listOf(itemId1, itemId2),
                merchantId
            )
        } returns listOf(merchantItem1)

        val exception = assertThrows(ItemNotFoundException::class.java) {
            logisticsService.createTask(createTaskDTO, consumerDetails)
        }

        assertEquals(
            "One or more ordered items not found for the given merchant with ID: $merchantId",
            exception.message
        )
        verify(exactly = 1) { consumerDetails.accountId }
        verify(atLeast = 1) { createTaskDTO.merchantId }
        verify(atLeast = 1) { createTaskDTO.selectedItems }
        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) {
            merchantItemRepository.findByIdInAndMerchantProfile_Id(
                listOf(itemId1, itemId2),
                merchantId
            )
        }
        confirmVerified(
            consumerDetails,
            createTaskDTO,
            consumerProfileRepository,
            merchantProfileRepository,
            merchantItemRepository,
            consumerProfile,
            merchantProfile,
            merchantItem1
        )
    }

    @Test
    fun `readyForAssignment should update task status and return mapped response`() {
        val merchantId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val merchantDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val savedTask = mockk<Task>()
        val updatedTask = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()

        every { merchantDetails.accountId } returns merchantId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndMerchantProfile_IdAndTaskStatus(
                taskId,
                merchantId,
                TaskStatus.CREATED
            )
        } returns savedTask
        every { savedTask.taskStatus = TaskStatus.READY_FOR_ASSIGNMENT } just Runs
        every { taskRepository.save(savedTask) } returns updatedTask
        every { updatedTask.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.readyForAssignment(taskStatusActionDTO, merchantDetails)

        assertSame(taskDataDTO, result)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { merchantDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndMerchantProfile_IdAndTaskStatus(
                taskId,
                merchantId,
                TaskStatus.CREATED
            )
        }
        verify(exactly = 1) { savedTask.taskStatus = TaskStatus.READY_FOR_ASSIGNMENT }
        verify(exactly = 1) { taskRepository.save(savedTask) }
        verify(exactly = 1) { updatedTask.toTaskDataDTO() }
        confirmVerified(taskStatusActionDTO, merchantDetails, taskRepository, savedTask, updatedTask, taskDataDTO)
    }

    @Test
    fun `readyForAssignment should throw TaskNotFoundException when task does not exist`() {
        val merchantId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val merchantDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()

        every { merchantDetails.accountId } returns merchantId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndMerchantProfile_IdAndTaskStatus(
                taskId,
                merchantId,
                TaskStatus.CREATED
            )
        } returns null

        val exception = assertThrows(TaskNotFoundException::class.java) {
            logisticsService.readyForAssignment(taskStatusActionDTO, merchantDetails)
        }

        assertEquals("Task not found with ID: $taskId for the given merchant", exception.message)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { merchantDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndMerchantProfile_IdAndTaskStatus(
                taskId,
                merchantId,
                TaskStatus.CREATED
            )
        }
        confirmVerified(taskStatusActionDTO, merchantDetails, taskRepository)
    }

    @Test
    fun `acceptAssignedToCourierTask should update task status to IN_PICKUP and return mapped response`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val savedTask = mockk<Task>()
        val updatedTask = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER
            )
        } returns savedTask
        every { savedTask.taskStatus = TaskStatus.IN_PICKUP } just Runs
        every { taskRepository.save(savedTask) } returns updatedTask
        every { updatedTask.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.acceptAssignedToCourierTask(taskStatusActionDTO, courierDetails)

        assertSame(taskDataDTO, result)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER
            )
        }
        verify(exactly = 1) { savedTask.taskStatus = TaskStatus.IN_PICKUP }
        verify(exactly = 1) { taskRepository.save(savedTask) }
        verify(exactly = 1) { updatedTask.toTaskDataDTO() }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository, savedTask, updatedTask, taskDataDTO)
    }

    @Test
    fun `acceptAssignedToCourierTask should throw TaskNotFoundException when task does not exist`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER
            )
        } returns null

        val exception = assertThrows(TaskNotFoundException::class.java) {
            logisticsService.acceptAssignedToCourierTask(taskStatusActionDTO, courierDetails)
        }

        assertEquals("Task not found with ID: $taskId for the given courier", exception.message)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.ASSIGNED_TO_COURIER
            )
        }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository)
    }

    @Test
    fun `completePickupTask should update task status to IN_DROPOFF and return mapped response`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val savedTask = mockk<Task>()
        val updatedTask = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_PICKUP
            )
        } returns savedTask
        every { savedTask.taskStatus = TaskStatus.IN_DROPOFF } just Runs
        every { taskRepository.save(savedTask) } returns updatedTask
        every { updatedTask.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.completePickupTask(taskStatusActionDTO, courierDetails)

        assertSame(taskDataDTO, result)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_PICKUP
            )
        }
        verify(exactly = 1) { savedTask.taskStatus = TaskStatus.IN_DROPOFF }
        verify(exactly = 1) { taskRepository.save(savedTask) }
        verify(exactly = 1) { updatedTask.toTaskDataDTO() }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository, savedTask, updatedTask, taskDataDTO)
    }

    @Test
    fun `completePickupTask should throw TaskNotFoundException when task does not exist`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_PICKUP
            )
        } returns null

        val exception = assertThrows(TaskNotFoundException::class.java) {
            logisticsService.completePickupTask(taskStatusActionDTO, courierDetails)
        }

        assertEquals("Task not found with ID: $taskId for the given courier", exception.message)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_PICKUP
            )
        }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository)
    }

    @Test
    fun `completeDropOffTask should update task status to DELIVERED and return mapped response`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val savedTask = mockk<Task>()
        val updatedTask = mockk<Task>()
        val taskDataDTO = mockk<TaskDataDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_DROPOFF
            )
        } returns savedTask
        every { savedTask.taskStatus = TaskStatus.DELIVERED } just Runs
        every { taskRepository.save(savedTask) } returns updatedTask
        every { updatedTask.toTaskDataDTO() } returns taskDataDTO

        val result = logisticsService.completeDropOffTask(taskStatusActionDTO, courierDetails)

        assertSame(taskDataDTO, result)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_DROPOFF
            )
        }
        verify(exactly = 1) { savedTask.taskStatus = TaskStatus.DELIVERED }
        verify(exactly = 1) { taskRepository.save(savedTask) }
        verify(exactly = 1) { updatedTask.toTaskDataDTO() }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository, savedTask, updatedTask, taskDataDTO)
    }

    @Test
    fun `completeDropOffTask should throw TaskNotFoundException when task does not exist`() {
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId.toString()
        every {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_DROPOFF
            )
        } returns null

        val exception = assertThrows(TaskNotFoundException::class.java) {
            logisticsService.completeDropOffTask(taskStatusActionDTO, courierDetails)
        }

        assertEquals("Task not found with ID: $taskId for the given courier", exception.message)
        verify(atLeast = 1) { taskStatusActionDTO.taskId }
        verify(atLeast = 1) { courierDetails.accountId }
        verify(exactly = 1) {
            taskRepository.findByIdAndCourierProfile_IdAndTaskStatus(
                taskId,
                courierId,
                TaskStatus.IN_DROPOFF
            )
        }
        confirmVerified(taskStatusActionDTO, courierDetails, taskRepository)
    }
}