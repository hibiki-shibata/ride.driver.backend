package com.ride.driver.backend.merchant.service

import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.mapper.toMerchantOrderHistoryDto
import com.ride.driver.backend.merchant.mapper.toMerchantProfileResDto
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Optional
import java.util.UUID
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class MerchantProfileServiceTest {

    private lateinit var merchantProfileRepository: MerchantProfileRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var merchantProfileService: MerchantProfileService

    @BeforeEach
    fun setUp() {
        merchantProfileRepository = mockk()
        taskRepository = mockk()
        merchantProfileService = MerchantProfileService(
            merchantProfileRepository = merchantProfileRepository,
            taskRepository = taskRepository
        )

        mockkStatic(MerchantProfile::toMerchantProfileResDto)
        mockkStatic(Task::toMerchantOrderHistoryDto)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getMerchantProfile should return merchant profile response`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantProfileResDTO = mockk<MerchantProfileResDTO>()

        every { merchantDetails.accountId } returns merchantId
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.id } returns merchantId
        every { merchantProfile.toMerchantProfileResDto() } returns merchantProfileResDTO

        val result = merchantProfileService.getMerchantProfile(merchantDetails)

        assertSame(merchantProfileResDTO, result)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { merchantProfile.id }
        verify(exactly = 1) { merchantProfile.toMerchantProfileResDto() }
        confirmVerified(merchantDetails, merchantProfileRepository, merchantProfile, merchantProfileResDTO)
    }

    @Test
    fun `getMerchantProfile should throw AccountNotFoundException when merchant does not exist`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()

        every { merchantDetails.accountId } returns merchantId
        every { merchantProfileRepository.findById(merchantId) } returns Optional.empty()

        val exception = assertThrows(AccountNotFoundException::class.java) {
            merchantProfileService.getMerchantProfile(merchantDetails)
        }

        assertEquals("Merchant not found with ID: $merchantId", exception.message)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        confirmVerified(merchantDetails, merchantProfileRepository)
    }

    @Test
    fun `updateMerchantProfile should update merchant profile and return response`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val req = mockk<MerchantProfileReqDTO>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantProfileResDTO = mockk<MerchantProfileResDTO>()
        val coordinate = mockk<Coordinate>()

        every { merchantDetails.accountId } returns merchantId
        every { req.name } returns "updated-name"
        every { req.phoneNumber } returns "01012345678"
        every { req.merchantAddress } returns "updated-address"
        every { req.merchantComments } returns "updated-comments"
        every { req.merchantAddressCoordinate } returns coordinate

        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.id } returns merchantId
        every { merchantProfile.name = any() } just Runs
        every { merchantProfile.phoneNumber = any() } just Runs
        every { merchantProfile.merchantAddress = any() } just Runs
        every { merchantProfile.merchantComments = any() } just Runs
        every { merchantProfile.merchantAddressCoordinate = any() } just Runs
        every { merchantProfileRepository.save(merchantProfile) } returns merchantProfile
        every { merchantProfile.toMerchantProfileResDto() } returns merchantProfileResDTO

        val result = merchantProfileService.updateMerchantProfile(merchantDetails, req)

        assertSame(merchantProfileResDTO, result)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { req.name }
        verify(exactly = 1) { req.phoneNumber }
        verify(exactly = 1) { req.merchantAddress }
        verify(exactly = 1) { req.merchantComments }
        verify(exactly = 1) { req.merchantAddressCoordinate }
        verify(exactly = 1) { merchantProfile.name = "updated-name" }
        verify(exactly = 1) { merchantProfile.phoneNumber = "01012345678" }
        verify(exactly = 1) { merchantProfile.merchantAddress = "updated-address" }
        verify(exactly = 1) { merchantProfile.merchantComments = "updated-comments" }
        verify(exactly = 1) { merchantProfile.merchantAddressCoordinate = coordinate }
        verify(exactly = 1) { merchantProfileRepository.save(merchantProfile) }
        verify(exactly = 1) { merchantProfile.id }
        verify(exactly = 1) { merchantProfile.toMerchantProfileResDto() }
        confirmVerified(merchantDetails, req, merchantProfileRepository, merchantProfile, merchantProfileResDTO, coordinate)
    }

    @Test
    fun `updateMerchantOpenStatus should set merchant status to OPEN when req isOpen is true`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val req = mockk<MerchantProfileReqDTO>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantProfileResDTO = mockk<MerchantProfileResDTO>()

        every { merchantDetails.accountId } returns merchantId
        every { req.isOpen } returns true
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.merchantStatus } returns MerchantStatus.CLOSED
        every { merchantProfile.id } returns merchantId
        every { merchantProfile.merchantStatus = any() } just Runs
        every { merchantProfileRepository.save(merchantProfile) } returns merchantProfile
        every { merchantProfile.toMerchantProfileResDto() } returns merchantProfileResDTO

        val result = merchantProfileService.updateMerchantOpenStatus(merchantDetails, req)

        assertSame(merchantProfileResDTO, result)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { merchantProfile.merchantStatus }
        verify(exactly = 1) { req.isOpen }
        verify(exactly = 1) { merchantProfile.merchantStatus = MerchantStatus.OPEN }
        verify(exactly = 1) { merchantProfileRepository.save(merchantProfile) }
        verify(exactly = 1) { merchantProfile.id }
        verify(exactly = 1) { merchantProfile.toMerchantProfileResDto() }
        confirmVerified(merchantDetails, req, merchantProfileRepository, merchantProfile, merchantProfileResDTO)
    }

    @Test
    fun `updateMerchantOpenStatus should set merchant status to CLOSED when req isOpen is false`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val req = mockk<MerchantProfileReqDTO>()
        val merchantProfile = mockk<MerchantProfile>()
        val merchantProfileResDTO = mockk<MerchantProfileResDTO>()

        every { merchantDetails.accountId } returns merchantId
        every { req.isOpen } returns false
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.merchantStatus } returns MerchantStatus.OPEN
        every { merchantProfile.id } returns merchantId
        every { merchantProfile.merchantStatus = any() } just Runs
        every { merchantProfileRepository.save(merchantProfile) } returns merchantProfile
        every { merchantProfile.toMerchantProfileResDto() } returns merchantProfileResDTO

        val result = merchantProfileService.updateMerchantOpenStatus(merchantDetails, req)

        assertSame(merchantProfileResDTO, result)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { merchantProfile.merchantStatus }
        verify(exactly = 1) { req.isOpen }
        verify(exactly = 1) { merchantProfile.merchantStatus = MerchantStatus.CLOSED }
        verify(exactly = 1) { merchantProfileRepository.save(merchantProfile) }
        verify(exactly = 1) { merchantProfile.id }
        verify(exactly = 1) { merchantProfile.toMerchantProfileResDto() }
        confirmVerified(merchantDetails, req, merchantProfileRepository, merchantProfile, merchantProfileResDTO)
    }

    @Test
    fun `updateMerchantOpenStatus should throw AccountInvalidValuesException when merchant is admin only`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val req = mockk<MerchantProfileReqDTO>()
        val merchantProfile = mockk<MerchantProfile>()

        every { merchantDetails.accountId } returns merchantId
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.merchantStatus } returns MerchantStatus.ADMINS_ONLY

        val exception = assertThrows(AccountInvalidValuesException::class.java) {
            merchantProfileService.updateMerchantOpenStatus(merchantDetails, req)
        }

        assertEquals("Cannot change open status for an admin-only merchant", exception.message)
        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { merchantProfile.merchantStatus }
        verify(exactly = 0) { merchantProfileRepository.save(any()) }
        confirmVerified(merchantDetails, req, merchantProfileRepository, merchantProfile)
    }

    @Test
    fun `deleteMerchantProfile should delete merchant profile`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val merchantProfile = mockk<MerchantProfile>()

        every { merchantDetails.accountId } returns merchantId
        every { merchantProfileRepository.findById(merchantId) } returns Optional.of(merchantProfile)
        every { merchantProfile.id } returns merchantId
        every { merchantProfileRepository.delete(merchantProfile) } just Runs

        merchantProfileService.deleteMerchantProfile(merchantDetails)

        verify(exactly = 1) { merchantDetails.accountId }
        verify(exactly = 1) { merchantProfileRepository.findById(merchantId) }
        verify(exactly = 1) { merchantProfileRepository.delete(merchantProfile) }
        verify(exactly = 1) { merchantProfile.id }
        confirmVerified(merchantDetails, merchantProfileRepository, merchantProfile)
    }

    @Test
    fun `getMerchantOrderHistory should return mapped task history`() {
        val merchantId = UUID.randomUUID()
        val merchantDetails = mockk<AccessTokenClaim>()
        val task1 = mockk<Task>()
        val task2 = mockk<Task>()
        val taskHistory1 = mockk<MerchantOrderHistoryDTO>()
        val taskHistory2 = mockk<MerchantOrderHistoryDTO>()

        every { merchantDetails.accountId } returns merchantId
        every {
            taskRepository.findByMerchantProfile_Id(merchantId, PageRequest.of(0, 100))
        } returns PageImpl(listOf(task1, task2))
        every { task1.toMerchantOrderHistoryDto() } returns taskHistory1
        every { task2.toMerchantOrderHistoryDto() } returns taskHistory2

        val result = merchantProfileService.getMerchantOrderHistory(merchantDetails)

        assertEquals(listOf(taskHistory1, taskHistory2), result)
    }
}