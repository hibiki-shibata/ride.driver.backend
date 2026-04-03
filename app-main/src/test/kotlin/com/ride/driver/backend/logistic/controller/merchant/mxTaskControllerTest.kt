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
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MerchantTaskControllerTest {

    private lateinit var logisticsService: LogisticsService
    private lateinit var merchantTaskController: MerchantTaskController

    @BeforeEach
    fun setUp() {
        logisticsService = mockk()
        merchantTaskController = MerchantTaskController(
            logisticsService = logisticsService
        )
    }

    @Test
    fun `MxReadyTaskForAssignment should return ok response with updated task`() {
        val merchantDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val merchantId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()

        every { merchantDetails.accountId } returns merchantId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.readyForAssignment(
                taskStatusActionDTO = taskStatusActionDTO,
                merchantDetails = merchantDetails
            )
        } returns taskDataDTO

        val result = merchantTaskController.MxReadyTaskForAssignment(
            taskStatusActionDTO = taskStatusActionDTO,
            merchantDetails = merchantDetails
        )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.readyForAssignment(
                taskStatusActionDTO = taskStatusActionDTO,
                merchantDetails = merchantDetails
            )
        }
        confirmVerified(logisticsService, merchantDetails, taskStatusActionDTO, taskDataDTO)
    }

    @Test
    fun `MxReadyTaskForAssignment should propagate exception when service throws`() {
        val merchantDetails = mockk<AccessTokenClaim>()
        val taskStatusActionDTO = mockk<TaskStatusActionDTO>()
        val merchantId = UUID.randomUUID()
        val taskId = UUID.randomUUID().toString()
        val exception = TaskNotFoundException("Task not found with ID: $taskId for the given merchant")

        every { merchantDetails.accountId } returns merchantId
        every { taskStatusActionDTO.taskId } returns taskId
        every {
            logisticsService.readyForAssignment(
                taskStatusActionDTO = taskStatusActionDTO,
                merchantDetails = merchantDetails
            )
        } throws exception

        val result = assertThrows(TaskNotFoundException::class.java) {
            merchantTaskController.MxReadyTaskForAssignment(
                taskStatusActionDTO = taskStatusActionDTO,
                merchantDetails = merchantDetails
            )
        }

        assertSame(exception, result)

        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { taskStatusActionDTO.taskId }
        verify(exactly = 1) {
            logisticsService.readyForAssignment(
                taskStatusActionDTO = taskStatusActionDTO,
                merchantDetails = merchantDetails
            )
        }
        confirmVerified(logisticsService, merchantDetails, taskStatusActionDTO)
    }
}