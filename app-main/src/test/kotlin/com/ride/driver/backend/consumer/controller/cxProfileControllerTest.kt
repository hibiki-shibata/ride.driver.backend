package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.domain.ServiceType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ConsumerProfileControllerUnitTest {

    private val consumerProfileService = mockk<ConsumerProfileService>()
    private val controller = ConsumerProfileController(consumerProfileService)

    @Test
    fun `getConsumerProfile returns 200 with body`() {
        val consumerId = UUID.randomUUID()

        val claim = AccessTokenClaim(
            accountId = consumerId,
            accountName = "hibiki",
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE),
            serviceType = ServiceType.CONSUMER
        )

        val response = ConsumerProfileResDTO(
            id = "UUID is expected here",
            name = "Hibiki",
            emailAddress = "hibiki@example.com",
            consumerAddress = "Tokyo",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917)
        )

        every { consumerProfileService.getConsumerProfile(claim) } returns response

        val result = controller.getConsumerProfile(claim)
        println(result.body)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(response, result.body)
    }
}