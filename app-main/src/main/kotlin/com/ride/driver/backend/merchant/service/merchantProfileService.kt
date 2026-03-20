package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository

class MerchantProfileService (
    val passwordService: PasswordService,
    val merchantProfileRepository: MerchantProfileRepository
) {
    fun registerNewMerchant(
        merchantName: String,
        phoneNumber: String,
        password: String,
        merchantAddress: String,
        merchantComments: String?,
        addressCoordinate: Coordinate
    ): MerchantProfile {
        val isMerchantExists: Boolean = merchantProfileRepository.existsByPhoneNumber(phoneNumber)
        if (isMerchantExists) throw AccountConflictException("Merchant with phone number ${phoneNumber} already exists")
        val savedNewMerchant: MerchantProfile = merchantProfileRepository.save(
                MerchantProfile(
                name = merchantName,
                phoneNumber = phoneNumber,
                passwordHash = passwordService.hashPassword(password),
                merchantAddress = merchantAddress,
                merchantComments = merchantComments,
                merchantStatus = MerchantStatus.ADMINS_ONLY,
                merchantAddressCoordinate = addressCoordinate
            )
        )     
        return savedNewMerchant        
    }    
}