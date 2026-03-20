package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import java.util.UUID

@Service
class MerchantProfileService (
    val merchantProfileRepository: MerchantProfileRepository,
    val passwordService: PasswordService,
    val taskRepository: TaskRepository
) {
    fun getMerchantProfile(merchantId: UUID): MerchantProfile? {
        return merchantProfileRepository.findById(merchantId) ?: throw AccountNotFoundException("Merchant not found with ID: $merchantId")
    }

    fun updateMerchantProfile(
        merchantId: UUID,
        newName: String?,
        newPhoneNumber: String?,
        newMerchantAddress: String?,
        newMerchantComments: String?,
        newMerchantStatus: MerchantStatus?,
        newMerchantAddressCoordinate: Coordinate?
    ): MerchantProfile {
        val existingProfile = merchantProfileRepository.findById(merchantId) ?: throw AccountNotFoundException("Merchant not found with ID: $merchantId")
        val updatedProfile = existingProfile.copy(
            name = newName ?: existingProfile.name,
            phoneNumber = newPhoneNumber ?: existingProfile.phoneNumber,
            merchantAddress = newMerchantAddress ?: existingProfile.merchantAddress,
            merchantComments = newMerchantComments ?: existingProfile.merchantComments,
            merchantStatus = newMerchantStatus ?: existingProfile.merchantStatus,
            merchantAddressCoordinate = newMerchantAddressCoordinate ?: existingProfile.merchantAddressCoordinate
        )
        merchantProfileRepository.save(updatedProfile)
        return updatedProfile
    }

    fun getMerchantOrderHistory(merchantId: UUID): List<Task> {
        return taskRepository.findByMerchantProfile_Id(merchantId).sortedByDescending { it.orderTime }
    }      
}