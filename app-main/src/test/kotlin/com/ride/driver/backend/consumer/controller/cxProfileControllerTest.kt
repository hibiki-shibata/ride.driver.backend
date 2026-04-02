package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ConsumerProfileControllerUnitTest(
) {
    private val consumerProfileService = mockk<ConsumerProfileService>()
    private val consumerProfileController = ConsumerProfileController(consumerProfileService)

    val consumerId = UUID.randomUUID()
    val accessTokenClaim = AccessTokenClaim(
        accountId = consumerId,
        accountName = "hibiki",
        accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE),
        serviceType = ServiceType.CONSUMER
    )

    val response = ConsumerProfileResDTO(
        id = consumerId.toString(),
        name = "Hibiki",
        emailAddress = "hibiki@example.com",
        consumerAddress = "Tokyo",
        consumerAddressCoordinate = Coordinate(35.6895, 139.6917)
    )

    @Test
    fun `getConsumerProfile returns 200 with body`() {

        every { consumerProfileService.getConsumerProfile(accessTokenClaim) } returns response

        val result = consumerProfileController.getConsumerProfile(accessTokenClaim)
        println(result.body)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(response, result.body)
    }

    @Test
    fun `getConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileService.getConsumerProfile(accessTokenClaim) } throws AccountNotFoundException("Consumer not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerProfileController.getConsumerProfile(accessTokenClaim)
        }
    }

    @Test
    fun `getConsumerProfile throws generic exception when unexpected error occurs`() {
        every { consumerProfileService.getConsumerProfile(accessTokenClaim) } throws RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerProfileController.getConsumerProfile(accessTokenClaim)
        }
    }
} 