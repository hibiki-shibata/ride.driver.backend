package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ConsumerProfileControllerUnitTest {

    private val consumerProfileService = mockk<ConsumerProfileService>()
    private val consumerProfileController = ConsumerProfileController(consumerProfileService)

    private val consumerDetails = mockk<AccessTokenClaim>()
    private val newConsumerProfileData = mockk<ConsumerProfileReqDTO>()
    private val fetchedConsumerProfile = mockk<ConsumerProfileResDTO>()
    private val updatedConsumerProfile = mockk<ConsumerProfileResDTO>()
    private val consumerOrderHistoryItem = mockk<ConsumerOrderHistoryDTO>()
    private val consumerOrderHistory = listOf(consumerOrderHistoryItem)

    init {
        every { consumerDetails.accountId } returns UUID.randomUUID()
    }

    @Test
    fun `getConsumerProfile returns 200 with body`() {
        every { consumerProfileService.getConsumerProfile(consumerDetails) } returns fetchedConsumerProfile

        val result = consumerProfileController.getConsumerProfile(consumerDetails)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(fetchedConsumerProfile, result.body)
    }

    @Test
    fun `getConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileService.getConsumerProfile(consumerDetails) } throws
            AccountNotFoundException("Consumer account not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerProfileController.getConsumerProfile(consumerDetails)
        }
    }

    @Test
    fun `getConsumerProfile throws RuntimeException when unexpected error occurs`() {
        every { consumerProfileService.getConsumerProfile(consumerDetails) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerProfileController.getConsumerProfile(consumerDetails)
        }
    }

    @Test
    fun `updateConsumerProfile returns 200 with body`() {
        every {
            consumerProfileService.updateConsumerProfile(
                consumerDetails = consumerDetails,
                newConsumerProfileData = newConsumerProfileData
            )
        } returns updatedConsumerProfile

        val result = consumerProfileController.updateConsumerProfile(
            newConsumerProfileData = newConsumerProfileData,
            consumerDetails = consumerDetails
        )

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(updatedConsumerProfile, result.body)
    }

    @Test
    fun `updateConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every {
            consumerProfileService.updateConsumerProfile(
                consumerDetails = consumerDetails,
                newConsumerProfileData = newConsumerProfileData
            )
        } throws AccountNotFoundException("Consumer account not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerProfileController.updateConsumerProfile(
                newConsumerProfileData = newConsumerProfileData,
                consumerDetails = consumerDetails
            )
        }
    }

    @Test
    fun `updateConsumerProfile throws RuntimeException when unexpected error occurs`() {
        every {
            consumerProfileService.updateConsumerProfile(
                consumerDetails = consumerDetails,
                newConsumerProfileData = newConsumerProfileData
            )
        } throws RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerProfileController.updateConsumerProfile(
                newConsumerProfileData = newConsumerProfileData,
                consumerDetails = consumerDetails
            )
        }
    }

    @Test
    fun `deleteConsumerProfile returns 204 with no body`() {
        every { consumerProfileService.deleteConsumerProfile(consumerDetails) } returns Unit

        val result = consumerProfileController.deleteConsumerProfile(consumerDetails)

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
        Assertions.assertEquals(null, result.body)
        verify(exactly = 1) { consumerProfileService.deleteConsumerProfile(consumerDetails) }
    }

    @Test
    fun `deleteConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileService.deleteConsumerProfile(consumerDetails) } throws
            AccountNotFoundException("Consumer account not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerProfileController.deleteConsumerProfile(consumerDetails)
        }
    }

    @Test
    fun `deleteConsumerProfile throws RuntimeException when unexpected error occurs`() {
        every { consumerProfileService.deleteConsumerProfile(consumerDetails) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerProfileController.deleteConsumerProfile(consumerDetails)
        }
    }

    @Test
    fun `getConsumerOrderHistory returns 200 with body`() {
        every { consumerProfileService.getConsumerOrderHistory(consumerDetails) } returns consumerOrderHistory

        val result = consumerProfileController.getConsumerOrderHistory(consumerDetails)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(consumerOrderHistory, result.body)
    }

    @Test
    fun `getConsumerOrderHistory returns 200 with empty list`() {
        every { consumerProfileService.getConsumerOrderHistory(consumerDetails) } returns emptyList()

        val result = consumerProfileController.getConsumerOrderHistory(consumerDetails)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(emptyList<ConsumerOrderHistoryDTO?>(), result.body)
    }

    @Test
    fun `getConsumerOrderHistory throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileService.getConsumerOrderHistory(consumerDetails) } throws
            AccountNotFoundException("Consumer account not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerProfileController.getConsumerOrderHistory(consumerDetails)
        }
    }

    @Test
    fun `getConsumerOrderHistory throws RuntimeException when unexpected error occurs`() {
        every { consumerProfileService.getConsumerOrderHistory(consumerDetails) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerProfileController.getConsumerOrderHistory(consumerDetails)
        }
    }
}