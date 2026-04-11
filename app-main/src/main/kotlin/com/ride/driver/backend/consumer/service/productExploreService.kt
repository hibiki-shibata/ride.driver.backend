package com.ride.driver.backend.consumer.service

import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.consumer.mapper.toMerchantProfileResDTO
import com.ride.driver.backend.consumer.mapper.toMerchantProductResDTO
import com.ride.driver.backend.consumer.dto.MerchantProductResDTO
import com.ride.driver.backend.consumer.dto.MerchantProfileResDTO
import com.ride.driver.backend.shared.exception.AccountNotFoundException

class ProductExploreService(
    private val merchantProfileRepository: MerchantProfileRepository,
    private val merchantItemRepository: MerchantItemRepository
){
    val logger: Logger = LoggerFactory.getLogger(ProductExploreService::class.java)

    fun getAvailableMerchants(
        page: Int,
        size: Int
    ): List<MerchantProfileResDTO> {
        val pageable = PageRequest.of(page, size)
        val savedMerchantProfiles: List<MerchantProfile> = merchantProfileRepository.findByMerchantStatus("ACTIVE", pageable).content
        return savedMerchantProfiles.map { it.toMerchantProfileResDTO() }
    }

    fun getMerchantProfile(
        merchantId: String
    ): MerchantProfileResDTO {
        logger.info("event=get_merchant_profile_started merchantId={}", merchantId)
        val merchantProfile: MerchantProfile = merchantProfileRepository.findById(UUID.fromString(merchantId))
            .orElseThrow { AccountNotFoundException("Merchant with ID $merchantId not found") }
        return merchantProfile.toMerchantProfileResDTO()
    }

    fun getProductsByMerchant(merchantId: String): List<MerchantProductResDTO> {
        logger.info("event=get_products_by_merchant_started merchantId={}", merchantId)
        val merchantItems: List<MerchantItem> = merchantItemRepository.findByMerchantProfile_Id(UUID.fromString(merchantId))
        return merchantItems.map { it.toMerchantProductResDTO() }
    }
}