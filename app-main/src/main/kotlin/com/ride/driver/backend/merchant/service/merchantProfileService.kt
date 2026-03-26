package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
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
import com.ride.driver.backend.shared.exception.AccountInvalidInputException
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID



@Service
class MerchantProfileService (
    val merchantProfileRepository: MerchantProfileRepository,
    val passwordService: PasswordService,
    val taskRepository: TaskRepository
) {
    fun getMerchantProfile(merchantDetails: AccessTokenClaim): MerchantProfileResDTO {
        val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
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
        return updatedProfile.toMerchantProfileResDto()
    }

    @Transactional
    fun updateMerchantOpenStatus(
        merchantDetails: AccessTokenClaim,
        req: MerchantProfileReqDTO
    ): MerchantProfileResDTO {
        val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
        if (savedMerchant.merchantStatus == MerchantStatus.ADMINS_ONLY) throw AccountInvalidInputException("Cannot change open status for an admin-only merchant")
        savedMerchant.apply{
            merchantStatus = if (req.isOpen) MerchantStatus.OPEN else MerchantStatus.CLOSED
        }
        val updatedProfile: MerchantProfile = merchantProfileRepository.save(savedMerchant)
        return updatedProfile.toMerchantProfileResDto()
    }

    fun getMerchantOrderHistory(
        merchantDetails: AccessTokenClaim
    ): List<MerchantOrderHistoryDTO> {
        val taskHistory: List<Task> = taskRepository.findByMerchantProfile_Id(merchantDetails.accountId).sortedByDescending { it.orderTime }
        return taskHistory.map {it.toMerchantOrderHistoryDto()}
    }

    private fun getMerchantProfileById(merchantId: UUID): MerchantProfile {
            val savedMerchant = merchantProfileRepository.findById(merchantId).orElseThrow { 
                AccountNotFoundException("Merchant not found with ID: $merchantId") 
            }
            return savedMerchant
    }              
}