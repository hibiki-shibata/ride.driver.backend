package com.ride.driver.backend.auth.services

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.merchant.models.MerchantStatus
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository


@Service
class MerchantAuthService(
    private val merchantProfileRepository: MerchantProfileRepository
) {
    fun registerNewMerchant(
        name: String,
        phoneNumber: String,
        password: String,
        merchantAddress: String,
        merchantAddressCoordinate: Coordinate        
    ): MerchantProfile {
        val isMerchantExists: Boolean = merchantProfileRepository.existsByPhoneNumber(phoneNumber)
        if (isMerchantExists) throw BadRequestException("Merchant with phone number ${phoneNumber} already exists")
        val savedMerchant: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = name,
                phoneNumber = phoneNumber,
                merchantAddress = merchantAddress,
                merchantAddressCoordiate = merchantAddressCoordinate,
                passwordHash = password.hashCode().toString(), // Simple hash for demonstration. Use a proper hashing algorithm in production.
                merchantStatus = MerchantStatus.CLOSED
            )
        )
        return savedMerchant
    }

    fun getMerchantProfileByPhoneNumber(phoneNumber: String): MerchantProfile {
        val savedMerchant: MerchantProfile = merchantProfileRepository.findByPhoneNumber(phoneNumber) ?:
            throw BadRequestException("Merchant with phone number ${phoneNumber} does not exist. Please sign up first.")
        return savedMerchant
    }
}