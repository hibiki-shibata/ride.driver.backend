package com.ride.driver.backend.merchant.controller

import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.service.MerchantProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MerchantProfileControllerTest {

    private lateinit var merchantProfileService: MerchantProfileService
    private lateinit var merchantProfileController: MerchantProfileController

    @BeforeEach
    fun setUp() {
        merchantProfileService = mockk()
        merchantProfileController = MerchantProfileController(merchantProfileService)
    }

    @Test
    fun `getMerchantProfile should return 200 ok with merchant profile`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val merchantProfile = mockk<MerchantProfileResDTO>()

        every { merchantProfileService.getMerchantProfile(merchantDetails) } returns merchantProfile

        val response = merchantProfileController.getMerchantProfile(merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(merchantProfile, response.body)

        verify(exactly = 1) { merchantProfileService.getMerchantProfile(merchantDetails) }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `getMerchantProfile should propagate exception when service throws`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("merchant profile not found")

        every { merchantProfileService.getMerchantProfile(merchantDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantProfileController.getMerchantProfile(merchantDetails)
        }

        assertEquals("merchant profile not found", thrown.message)

        verify(exactly = 1) { merchantProfileService.getMerchantProfile(merchantDetails) }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `updateMerchantProfile should return 200 ok with updated profile`() {
        val request = mockk<MerchantProfileReqDTO>()
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedProfile = mockk<MerchantProfileResDTO>()

        every {
            merchantProfileService.updateMerchantProfile(
                merchantDetails = merchantDetails,
                req = request
            )
        } returns updatedProfile

        val response = merchantProfileController.updateMerchantProfile(request, merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(updatedProfile, response.body)

        verify(exactly = 1) {
            merchantProfileService.updateMerchantProfile(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `updateMerchantProfile should propagate exception when service throws`() {
        val request = mockk<MerchantProfileReqDTO>()
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("merchant profile update failed")

        every {
            merchantProfileService.updateMerchantProfile(
                merchantDetails = merchantDetails,
                req = request
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantProfileController.updateMerchantProfile(request, merchantDetails)
        }

        assertEquals("merchant profile update failed", thrown.message)

        verify(exactly = 1) {
            merchantProfileService.updateMerchantProfile(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `deleteMerchantProfile should return 204 no content`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)

        every { merchantProfileService.deleteMerchantProfile(merchantDetails) } returns Unit

        val response = merchantProfileController.deleteMerchantProfile(merchantDetails)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)

        verify(exactly = 1) { merchantProfileService.deleteMerchantProfile(merchantDetails) }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `deleteMerchantProfile should propagate exception when service throws`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("merchant profile delete failed")

        every { merchantProfileService.deleteMerchantProfile(merchantDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantProfileController.deleteMerchantProfile(merchantDetails)
        }

        assertEquals("merchant profile delete failed", thrown.message)

        verify(exactly = 1) { merchantProfileService.deleteMerchantProfile(merchantDetails) }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `updateMerchantOpenStatus should return 200 ok with updated profile`() {
        val request = mockk<MerchantProfileReqDTO>()
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedProfile = mockk<MerchantProfileResDTO>()

        every {
            merchantProfileService.updateMerchantOpenStatus(
                merchantDetails = merchantDetails,
                req = request
            )
        } returns updatedProfile

        val response = merchantProfileController.updateMerchantOpenStatus(request, merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(updatedProfile, response.body)

        verify(exactly = 1) {
            merchantProfileService.updateMerchantOpenStatus(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `updateMerchantOpenStatus should propagate exception when service throws`() {
        val request = mockk<MerchantProfileReqDTO>()
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("merchant open status update failed")

        every {
            merchantProfileService.updateMerchantOpenStatus(
                merchantDetails = merchantDetails,
                req = request
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantProfileController.updateMerchantOpenStatus(request, merchantDetails)
        }

        assertEquals("merchant open status update failed", thrown.message)

        verify(exactly = 1) {
            merchantProfileService.updateMerchantOpenStatus(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `getMerchantOrderHistory should return 200 ok with order history`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val merchantOrderHistory: List<MerchantOrderHistoryDTO?> = listOf(mockk(), null)

        every { merchantProfileService.getMerchantOrderHistory(merchantDetails) } returns merchantOrderHistory

        val response = merchantProfileController.getMerchantOrderHistory(merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(merchantOrderHistory, response.body)

        verify(exactly = 1) { merchantProfileService.getMerchantOrderHistory(merchantDetails) }
        confirmVerified(merchantProfileService)
    }

    @Test
    fun `getMerchantOrderHistory should propagate exception when service throws`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("merchant order history fetch failed")

        every { merchantProfileService.getMerchantOrderHistory(merchantDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantProfileController.getMerchantOrderHistory(merchantDetails)
        }

        assertEquals("merchant order history fetch failed", thrown.message)

        verify(exactly = 1) { merchantProfileService.getMerchantOrderHistory(merchantDetails) }
        confirmVerified(merchantProfileService)
    }
}