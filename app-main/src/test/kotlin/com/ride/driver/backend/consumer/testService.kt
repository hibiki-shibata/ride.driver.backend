package com.ride.driver.backend.consumer.service

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class ConsumerProfileServiceTest {

    private lateinit var consumerProfileRepository: ConsumerProfileRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var consumerProfileService: ConsumerProfileService

    @BeforeEach
    fun setUp() {
        consumerProfileRepository = mockk()
        taskRepository = mockk()
        consumerProfileService = ConsumerProfileService(
            consumerProfileRepository,
            taskRepository
        )
    }

    @Test
    fun `should return consumer profile response when consumer exists`() {
        // given
        val emailAddress = "hibiki@gmail.com"
        val consumerId = UUID.randomUUID()

        val accessTokenClaim = AccessTokenClaim(
            accountId = consumerId,
            accountName = "test_consumer",
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
        )

        val consumerProfile = ConsumerProfile(
            id = consumerId,
            name = "Hibiki",
            emailAddress = emailAddress,
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
            consumerAddress = "123 Main St",
            passwordHash = "hashed_password"
        )

        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(consumerProfile)

        // when
        val result: ConsumerProfileResDTO = consumerProfileService.getConsumerProfile(accessTokenClaim)

        // then
        assertEquals(emailAddress, result.emailAddress)
        // add more assertions based on your DTO fields
        // assertEquals("Hibiki", result.name)
        // assertEquals("hibiki@example.com", result.email)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
    }

    @Test
    fun `should throw AccountNotFoundException when consumer does not exist`() {
        // given
        val consumerId = UUID.randomUUID()

        val accessTokenClaim = AccessTokenClaim(
            accountId = consumerId,
            accountName = "test_consumer",
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
            
        )

        every { consumerProfileRepository.findById(consumerId) } returns Optional.empty()

        // when & then
        assertThrows(AccountNotFoundException::class.java) {
            consumerProfileService.getConsumerProfile(accessTokenClaim)
        }

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
    }
}