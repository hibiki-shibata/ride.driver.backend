package com.ride.driver.backend.merchant.service

import com.ride.driver.backend.merchant.dto.MerchantItemReqDTO
import com.ride.driver.backend.merchant.dto.MerchantItemResDTO
import com.ride.driver.backend.merchant.mapper.toMerchantItem
import com.ride.driver.backend.merchant.mapper.toMerchantItemResDTO
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.confirmVerified
import io.mockk.every
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

class MerchantItemServiceTest {

    private lateinit var merchantItemRepository: MerchantItemRepository
    private lateinit var merchantProfileRepository: MerchantProfileRepository
    private lateinit var merchantItemService: MerchantItemService

    @BeforeEach
    fun setUp() {
        merchantItemRepository = mockk()
        merchantProfileRepository = mockk()
        merchantItemService = MerchantItemService(
            merchantItemRepository = merchantItemRepository,
            merchantProfileRepository = merchantProfileRepository
        )

        mockkStatic(MerchantItem::toMerchantItemResDTO)
        mockkStatic(MerchantItemReqDTO::toMerchantItem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getMerchantItems should return mapped merchant items`() {
        val merchantDetails = mockk<AccessTokenClaim>()
        val item1 = mockk<MerchantItem>()
        val item2 = mockk<MerchantItem>()
        val res1 = mockk<MerchantItemResDTO>()
        val res2 = mockk<MerchantItemResDTO>()

        every { merchantDetails.accountId } returns UUID.randomUUID()
        every { merchantItemRepository.findByMerchantProfile_Id(any()) } returns listOf(item1, item2)
        every { item1.toMerchantItemResDTO() } returns res1
        every { item2.toMerchantItemResDTO() } returns res2

        val result = merchantItemService.getMerchantItems(merchantDetails)

        assertEquals(2, result.size)
        assertSame(res1, result[0])
        assertSame(res2, result[1])
    }

    @Test
    fun `getMerchantItems should return empty list when merchant has no items`() {
        val merchantDetails = mockk<AccessTokenClaim>()

        every { merchantDetails.accountId } returns UUID.randomUUID()
        every { merchantItemRepository.findByMerchantProfile_Id(any()) } returns emptyList()

        val result = merchantItemService.getMerchantItems(merchantDetails)

        assertEquals(emptyList<MerchantItemResDTO>(), result)

        verify(exactly = 1) { merchantItemRepository.findByMerchantProfile_Id(any()) }
        confirmVerified(merchantItemRepository, merchantProfileRepository)
    }

    @Test
    fun `updateMerchantItems should throw AccountNotFoundException when merchant does not exist`() {
        val merchantDetails = mockk<AccessTokenClaim>()
        val req1 = mockk<MerchantItemReqDTO>()
        val req2 = mockk<MerchantItemReqDTO>()
        val req = listOf(req1, req2)
        val merchantId = UUID.randomUUID()

        every { merchantDetails.accountId } returns merchantId
        every { merchantProfileRepository.findById(any()) } returns Optional.empty()

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            merchantItemService.updateMerchantItems(
                merchantDetails = merchantDetails,
                req = req
            )
        }

        assertEquals("Merchant not found with ID: $merchantId", thrown.message)

        verify(exactly = 1) { merchantProfileRepository.findById(any()) }
        verify(exactly = 0) { merchantItemRepository.saveAll(any<List<MerchantItem>>()) }
        confirmVerified(merchantItemRepository, merchantProfileRepository)
    }

    @Test
    fun `updateMerchantItems should create save and return mapped merchant items`() {
        val merchantDetails = mockk<AccessTokenClaim>()
        val merchantProfile = mockk<MerchantProfile>()
        val req1 = mockk<MerchantItemReqDTO>()
        val req2 = mockk<MerchantItemReqDTO>()
        val mappedItem1 = mockk<MerchantItem>()
        val mappedItem2 = mockk<MerchantItem>()
        val savedItem1 = mockk<MerchantItem>()
        val savedItem2 = mockk<MerchantItem>()
        val res1 = mockk<MerchantItemResDTO>()
        val res2 = mockk<MerchantItemResDTO>()

        every { merchantDetails.accountId } returns UUID.randomUUID()
        every { merchantProfileRepository.findById(any()) } returns Optional.of(merchantProfile)
        every { req1.toMerchantItem(any()) } returns mappedItem1
        every { req2.toMerchantItem(any()) } returns mappedItem2
        every { merchantItemRepository.saveAll(any<List<MerchantItem>>()) } returns listOf(savedItem1, savedItem2)
        every { savedItem1.toMerchantItemResDTO() } returns res1
        every { savedItem2.toMerchantItemResDTO() } returns res2

        val result = merchantItemService.updateMerchantItems(
            merchantDetails = merchantDetails,
            req = listOf(req1, req2)
        )

        assertEquals(2, result.size)
        assertSame(res1, result[0])
        assertSame(res2, result[1])
    }
}