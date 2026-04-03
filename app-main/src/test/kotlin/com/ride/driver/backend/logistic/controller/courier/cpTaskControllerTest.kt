package com.ride.driver.backend.logistic.controller

import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.TaskNotFoundException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CourierTaskControllerTest {

    private lateinit var logisticsService: LogisticsService
    private lateinit var courierTaskController: CourierTaskController

    @BeforeEach
    fun setUp() {
        logisticsService = mockk()
        courierTaskController = CourierTaskController(
            logisticsService = logisticsService
        )
    }

    @Test
    fun `pollCurrentTask should return ok response with assigned task`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val courierId = UUID.randomUUID()

        every { courierDetails.accountId } returns courierId
        every { logisticsService.pollTask(courierDetails) } returns taskDataDTO

        val result = courierTaskController.pollCurrentTask(courierDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { logisticsService.pollTask(courierDetails) }
        confirmVerified(logisticsService, courierDetails, taskDataDTO)
    }

    @Test
    fun `pollCurrentTask should return ok response with null body when no task exists`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val courierId = UUID.randomUUID()

        every { courierDetails.accountId } returns courierId
        every { logisticsService.pollTask(courierDetails) } returns null

        val result = courierTaskController.pollCurrentTask(courierDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertNull(result.body)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { logisticsService.pollTask(courierDetails) }
        confirmVerified(logisticsService, courierDetails)
    }

    @Test
    fun `pollCurrentTask should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val courierId = UUID.randomUUID()
        val exception = RuntimeException("unexpected error")

        every { courierDetails.accountId } returns courierId
        every { logisticsService.pollTask(courierDetails) } throws exception

        val result = assertThrows(RuntimeException::class.java) {
            courierTaskController.pollCurrentTask(courierDetails)
        }

        assertSame(exception, result)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { logisticsService.pollTask(courierDetails) }
        confirmVerified(logisticsService, courierDetails)
    }

    @Test
    fun `acceptTask should return ok response with updated task`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.acceptAssignedToCourierTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } returns taskDataDTO

        val result = courierTaskController.acceptTask(taskStatusActionDTO, courierDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.acceptAssignedToCourierTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO, taskDataDTO)
    }

    @Test
    fun `acceptTask should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()
        val exception = TaskNotFoundException("Task not found with ID: $taskId for the given courier")

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.acceptAssignedToCourierTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } throws exception

        val result = assertThrows(TaskNotFoundException::class.java) {
            courierTaskController.acceptTask(taskStatusActionDTO, courierDetails)
        }

        assertSame(exception, result)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.acceptAssignedToCourierTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO)
    }

    @Test
    fun `completePickup should return ok response with updated task`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.completePickupTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } returns taskDataDTO

        val result = courierTaskController.completePickup(taskStatusActionDTO, courierDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.completePickupTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO, taskDataDTO)
    }

    @Test
    fun `completePickup should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()
        val exception = TaskNotFoundException("Task not found with ID: $taskId for the given courier")

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.completePickupTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } throws exception

        val result = assertThrows(TaskNotFoundException::class.java) {
            courierTaskController.completePickup(taskStatusActionDTO, courierDetails)
        }

        assertSame(exception, result)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.completePickupTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO)
    }

    @Test
    fun `completeDropoff should return ok response with updated task`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.completeDropOffTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } returns taskDataDTO

        val result = courierTaskController.completeDropoff(taskStatusActionDTO, courierDetails)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.completeDropOffTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO, taskDataDTO)
    }

    @Test
    fun `completeDropoff should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val courierId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()
        val exception = TaskNotFoundException("Task not found with ID: $taskId for the given courier")

        every { courierDetails.accountId } returns courierId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.completeDropOffTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        } throws exception

        val result = assertThrows(TaskNotFoundException::class.java) {
            courierTaskController.completeDropoff(taskStatusActionDTO, courierDetails)
        }

        assertSame(exception, result)

        verify(exactly = 1) { courierDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.completeDropOffTask(
                taskStatusActionDTO = taskStatusActionDTO,
                courierDetails = courierDetails
            )
        }
        confirmVerified(logisticsService, courierDetails, taskStatusActionDTO)
    }
}