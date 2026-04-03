package com.ride.driver.backend.merchant.controller

import com.ride.driver.backend.merchant.dto.MerchantItemReqDTO
import com.ride.driver.backend.merchant.dto.MerchantItemResDTO
import com.ride.driver.backend.merchant.service.MerchantItemService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MerchantItemControllerTest {

    private lateinit var merchantItemService: MerchantItemService
    private lateinit var merchantItemController: MerchantItemController

    @BeforeEach
    fun setUp() {
        merchantItemService = mockk()
        merchantItemController = MerchantItemController(merchantItemService)
    }

    @Test
    fun `getMerchantItems should return 200 ok with merchant items`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val merchantItems = listOf(
            mockk<MerchantItemResDTO>(),
            mockk<MerchantItemResDTO>()
        )

        every { merchantItemService.getMerchantItems(merchantDetails) } returns merchantItems

        val response = merchantItemController.getMerchantItems(merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(merchantItems, response.body)

        verify(exactly = 1) { merchantItemService.getMerchantItems(merchantDetails) }
        confirmVerified(merchantItemService)
    }

    @Test
    fun `getMerchantItems should propagate exception when service throws`() {
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("failed to fetch merchant items")

        every { merchantItemService.getMerchantItems(merchantDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantItemController.getMerchantItems(merchantDetails)
        }

        assertEquals("failed to fetch merchant items", thrown.message)

        verify(exactly = 1) { merchantItemService.getMerchantItems(merchantDetails) }
        confirmVerified(merchantItemService)
    }

    @Test
    fun `updateMerchantItems should return 200 ok with updated merchant items`() {
        val request = listOf(
            mockk<MerchantItemReqDTO>(),
            mockk<MerchantItemReqDTO>()
        )
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedItems = listOf(
            mockk<MerchantItemResDTO>(),
            mockk<MerchantItemResDTO>()
        )

        every {
            merchantItemService.updateMerchantItems(
                merchantDetails = merchantDetails,
                req = request
            )
        } returns updatedItems

        val response = merchantItemController.updateMerchantItems(request, merchantDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(updatedItems, response.body)

        verify(exactly = 1) {
            merchantItemService.updateMerchantItems(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantItemService)
    }

    @Test
    fun `updateMerchantItems should propagate exception when service throws`() {
        val request = listOf(
            mockk<MerchantItemReqDTO>(),
            mockk<MerchantItemReqDTO>()
        )
        val merchantDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("failed to update merchant items")

        every {
            merchantItemService.updateMerchantItems(
                merchantDetails = merchantDetails,
                req = request
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantItemController.updateMerchantItems(request, merchantDetails)
        }

        assertEquals("failed to update merchant items", thrown.message)

        verify(exactly = 1) {
            merchantItemService.updateMerchantItems(
                merchantDetails = merchantDetails,
                req = request
            )
        }
        confirmVerified(merchantItemService)
    }
}