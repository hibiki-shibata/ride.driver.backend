package com.ride.driver.backend.merchant.service

import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.dto.MerchantItemReqDTO
import com.ride.driver.backend.merchant.dto.MerchantItemResDTO
import com.ride.driver.backend.merchant.mapper.toMerchantItem
import com.ride.driver.backend.merchant.mapper.toMerchantItemResDTO
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MerchantItemService (
    val merchantItemRepository: MerchantItemRepository,
    val merchantProfileRepository: MerchantProfileRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantItemService::class.java)

    fun getMerchantItems(
        merchantDetails: AccessTokenClaim
    ): List<MerchantItemResDTO> {
        val merchantId: UUID = merchantDetails.accountId
        val merchantItems: List<MerchantItem> = merchantItemRepository.findByMerchantProfile_Id(merchantId)
        logger.info("event=merchant_items_fetched merchantId={} itemCount={}", merchantId, merchantItems.size)
        return merchantItems.map { it.toMerchantItemResDTO() }
    }

    @Transactional
    fun updateMerchantItems(
        merchantDetails: AccessTokenClaim,
        req: List<MerchantItemReqDTO>
    ): List<MerchantItemResDTO> {
        val merchantProfile: MerchantProfile = merchantProfileRepository.findById(merchantDetails.accountId).orElseThrow {
            AccountNotFoundException("Merchant not found with ID: ${merchantDetails.accountId}")
        }
        val merchantItemsToCreate: List<MerchantItem> = req.map { it.toMerchantItem(merchantProfile) }
        val createdMerchantItems: List<MerchantItem> = merchantItemRepository.saveAll(merchantItemsToCreate)
        logger.info("event=merchant_items_created merchantId={} itemCount={}", merchantDetails.accountId, createdMerchantItems.size)
        return createdMerchantItems.map { it.toMerchantItemResDTO() }
    }
}