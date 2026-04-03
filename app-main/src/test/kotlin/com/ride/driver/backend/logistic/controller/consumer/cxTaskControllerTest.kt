package com.ride.driver.backend.logistic.controller

import com.ride.driver.backend.logistic.dto.CreateTaskDTO
import com.ride.driver.backend.logistic.dto.TaskDataDTO
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
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

class ConsumerTaskControllerTest {

    private lateinit var logisticsService: LogisticsService
    private lateinit var consumerTaskController: ConsumerTaskController

    @BeforeEach
    fun setUp() {
        logisticsService = mockk()
        consumerTaskController = ConsumerTaskController(
            logisticsService = logisticsService
        )
    }

    @Test
    fun `createTask should return ok response with created task`() {
        val createTaskDTO = mockk<CreateTaskDTO>()
        val consumerDetails = mockk<AccessTokenClaim>()
        val taskDataDTO = mockk<TaskDataDTO>()
        val consumerId = UUID.randomUUID()

        every { consumerDetails.accountId } returns consumerId
        every {
            logisticsService.createTask(
                createTaskDTO = createTaskDTO,
                consumerDetails = consumerDetails
            )
        } returns taskDataDTO

        val result = consumerTaskController.createTask(
            createTaskDTO = createTaskDTO,
            consumerDetails = consumerDetails
        )

        assertEquals(HttpStatus.OK, result.statusCode)
        assertSame(taskDataDTO, result.body)

        verify(exactly = 1) { consumerDetails.accountId }
        verify(exactly = 1) {
            logisticsService.createTask(
                createTaskDTO = createTaskDTO,
                consumerDetails = consumerDetails
            )
        }
        confirmVerified(logisticsService, consumerDetails, createTaskDTO, taskDataDTO)
    }

    @Test
    fun `createTask should propagate exception when service throws`() {
        val createTaskDTO = mockk<CreateTaskDTO>()
        val consumerDetails = mockk<AccessTokenClaim>()
        val consumerId = UUID.randomUUID()
        val exception = AccountNotFoundException("Consumer not found with ID: $consumerId")

        every { consumerDetails.accountId } returns consumerId
        every {
            logisticsService.createTask(
                createTaskDTO = createTaskDTO,
                consumerDetails = consumerDetails
            )
        } throws exception

        val result = assertThrows(AccountNotFoundException::class.java) {
            consumerTaskController.createTask(
                createTaskDTO = createTaskDTO,
                consumerDetails = consumerDetails
            )
        }

        assertSame(exception, result)

        verify(exactly = 1) { consumerDetails.accountId }
        verify(exactly = 1) {
            logisticsService.createTask(
                createTaskDTO = createTaskDTO,
                consumerDetails = consumerDetails
            )
        }
        confirmVerified(logisticsService, consumerDetails, createTaskDTO)
    }
}