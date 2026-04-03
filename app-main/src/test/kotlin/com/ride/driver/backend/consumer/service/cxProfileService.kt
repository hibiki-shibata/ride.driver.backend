package com.ride.driver.backend.consumer.service

import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import io.mockk.confirmVerified
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

@ExtendWith(MockKExtension::class)
class ConsumerProfileServiceTest {

    @MockK
    lateinit var consumerProfileRepository: ConsumerProfileRepository

    @MockK
    lateinit var taskRepository: TaskRepository

    @InjectMockKs
    lateinit var consumerProfileService: ConsumerProfileService

    private lateinit var consumerId: UUID
    private lateinit var accessTokenClaim: AccessTokenClaim
    private lateinit var savedConsumerProfile: ConsumerProfile
    private lateinit var updatedRequest: ConsumerProfileReqDTO

    @BeforeEach
    fun setUp() {
        consumerId = UUID.randomUUID()

        accessTokenClaim = AccessTokenClaim(
            accountId = consumerId,      
            accountName ="Hibiki Test Name",
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE),
            serviceType = ServiceType.CONSUMER
        )

        savedConsumerProfile = ConsumerProfile(
            id = consumerId,
            name = "Old Name",
            emailAddress = "old@email.com",
            consumerAddress = "Old Address",
            consumerAddressCoordinate = Coordinate(35.0000, 135.0000),
            passwordHash = "hashed Password"
            // add any other required fields for your entity
        )

        updatedRequest = ConsumerProfileReqDTO(
            name = "New Name",
            emailAddress = "new@email.com",
            consumerAddress = "New Address",
            consumerAddressCoordinate = Coordinate(35.6762, 139.6503),
            password = "newpassword123"
        )
    }

    @Test
    fun `getConsumerProfile returns profile response when consumer exists`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(savedConsumerProfile)

        val result = consumerProfileService.getConsumerProfile(accessTokenClaim)

        assertEquals(savedConsumerProfile.id, UUID.fromString(result.id))
        assertEquals(savedConsumerProfile.name, result.name)
        assertEquals(savedConsumerProfile.emailAddress, result.emailAddress)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `getConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            consumerProfileService.getConsumerProfile(accessTokenClaim)
        }

        assertEquals("Consumer not found with ID: $consumerId", exception.message)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `updateConsumerProfile updates and returns profile when email is changed and not duplicated`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(savedConsumerProfile)
        every { consumerProfileRepository.existsByEmailAddress(updatedRequest.emailAddress) } returns false
        every { consumerProfileRepository.save(savedConsumerProfile) } returns savedConsumerProfile

        val result = consumerProfileService.updateConsumerProfile(accessTokenClaim, updatedRequest)

        assertEquals(updatedRequest.name, result.name)
        assertEquals(updatedRequest.emailAddress, result.emailAddress)
        assertEquals(updatedRequest.consumerAddress, result.consumerAddress)
        assertEquals(updatedRequest.consumerAddressCoordinate, result.consumerAddressCoordinate)

        assertEquals("New Name", savedConsumerProfile.name)
        assertEquals("new@email.com", savedConsumerProfile.emailAddress)
        assertEquals("New Address", savedConsumerProfile.consumerAddress)
        assertEquals(updatedRequest.consumerAddressCoordinate, savedConsumerProfile.consumerAddressCoordinate)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { consumerProfileRepository.existsByEmailAddress(updatedRequest.emailAddress) }
        verify(exactly = 1) { consumerProfileRepository.save(savedConsumerProfile) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `updateConsumerProfile updates profile when email is unchanged`() {
        val sameEmailRequest = ConsumerProfileReqDTO(
            name = "Updated Name",
            emailAddress = savedConsumerProfile.emailAddress,
            consumerAddress = "Updated Address",
            consumerAddressCoordinate = Coordinate(11.1111, 22.2222),
            password = "updatedpassword123"
        )

        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(savedConsumerProfile)
        every { consumerProfileRepository.existsByEmailAddress(any()) } returns true
        every { consumerProfileRepository.save(savedConsumerProfile) } returns savedConsumerProfile

        val result = consumerProfileService.updateConsumerProfile(accessTokenClaim, sameEmailRequest)

        assertEquals("Updated Name", result.name)
        assertEquals(savedConsumerProfile.emailAddress, result.emailAddress)
        assertEquals("Updated Address", result.consumerAddress)
        assertEquals(sameEmailRequest.consumerAddressCoordinate, result.consumerAddressCoordinate)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { consumerProfileRepository.existsByEmailAddress(sameEmailRequest.emailAddress) }
        verify(exactly = 1) { consumerProfileRepository.save(savedConsumerProfile) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `updateConsumerProfile throws AccountConflictException when new email already exists`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(savedConsumerProfile)
        every { consumerProfileRepository.existsByEmailAddress(updatedRequest.emailAddress) } returns true

        val exception = assertThrows(AccountConflictException::class.java) {
            consumerProfileService.updateConsumerProfile(accessTokenClaim, updatedRequest)
        }

        assertEquals("Consumer with request email address already exists", exception.message)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { consumerProfileRepository.existsByEmailAddress(updatedRequest.emailAddress) }
        verify(exactly = 0) { consumerProfileRepository.save(any()) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `updateConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            consumerProfileService.updateConsumerProfile(accessTokenClaim, updatedRequest)
        }

        assertEquals("Consumer not found with ID: $consumerId", exception.message)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 0) { consumerProfileRepository.existsByEmailAddress(any()) }
        verify(exactly = 0) { consumerProfileRepository.save(any()) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `deleteConsumerProfile deletes consumer when found`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.of(savedConsumerProfile)
        every { consumerProfileRepository.delete(savedConsumerProfile) } returns Unit

        assertDoesNotThrow {
            consumerProfileService.deleteConsumerProfile(accessTokenClaim)
        }

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 1) { consumerProfileRepository.delete(savedConsumerProfile) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

    @Test
    fun `deleteConsumerProfile throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileRepository.findById(consumerId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            consumerProfileService.deleteConsumerProfile(accessTokenClaim)
        }

        assertEquals("Consumer not found with ID: $consumerId", exception.message)

        verify(exactly = 1) { consumerProfileRepository.findById(consumerId) }
        verify(exactly = 0) { consumerProfileRepository.delete(any()) }
        confirmVerified(consumerProfileRepository, taskRepository)
    }

}