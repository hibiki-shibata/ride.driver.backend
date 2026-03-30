package com.ride.driver.backend.merchant.service

import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import com.ride.driver.backend.merchant.mapper.toMerchantProfileResDto
import com.ride.driver.backend.merchant.mapper.toMerchantOrderHistoryDto
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository

@Service
class MerchantProfileService (
    val merchantProfileRepository: MerchantProfileRepository,
    val passwordService: PasswordService,
    val taskRepository: TaskRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantProfileService::class.java)

    fun getMerchantProfile(merchantDetails: AccessTokenClaim): MerchantProfileResDTO {
        val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
        logger.info("event=merchant_profile_fetched merchantId={}", savedMerchant.id)
        return savedMerchant.toMerchantProfileResDto()
    }

    @Transactional
    fun updateMerchantProfile(
        merchantDetails: AccessTokenClaim,
        req: MerchantProfileReqDTO
    ): MerchantProfileResDTO {
        val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
        savedMerchant.apply {
            name = req.name;
            phoneNumber = req.phoneNumber;
            merchantAddress = req.merchantAddress;
            merchantComments = req.merchantComments;
            merchantAddressCoordinate = req.merchantAddressCoordinate
        }
        val updatedProfile: MerchantProfile = merchantProfileRepository.save(savedMerchant)
        logger.info("event=merchant_profile_updated merchantId={}", savedMerchant.id)
        return updatedProfile.toMerchantProfileResDto()
    }

    @Transactional
    fun updateMerchantOpenStatus(
        merchantDetails: AccessTokenClaim,
        req: MerchantProfileReqDTO
    ): MerchantProfileResDTO {
        val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
        if (savedMerchant.merchantStatus == MerchantStatus.ADMINS_ONLY) throw AccountInvalidValuesException("Cannot change open status for an admin-only merchant")
        savedMerchant.apply{
            merchantStatus = if (req.isOpen) MerchantStatus.OPEN else MerchantStatus.CLOSED
        }
        val updatedProfile: MerchantProfile = merchantProfileRepository.save(savedMerchant)
        logger.info("event=merchant_online_status_updated merchantId={}", savedMerchant.id)
        return updatedProfile.toMerchantProfileResDto()
    }

    fun getMerchantOrderHistory(
        merchantDetails: AccessTokenClaim
    ): List<MerchantOrderHistoryDTO?> {
        val taskHistory: List<Task?> = taskRepository.findByMerchantProfile_Id(merchantDetails.accountId).sortedByDescending { it?.orderTime }
        logger.info("event=merchant_orderHistory_fetched merchantId={}", merchantDetails.accountId)
        return taskHistory.map {it?.toMerchantOrderHistoryDto()}
    }

    private fun getMerchantProfileById(merchantId: UUID): MerchantProfile {
            val savedMerchant = merchantProfileRepository.findById(merchantId).orElseThrow { 
                AccountNotFoundException("Merchant not found with ID: $merchantId") 
            }
            return savedMerchant
    }              
}