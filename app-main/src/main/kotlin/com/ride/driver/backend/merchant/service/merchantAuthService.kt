package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.service.PasswordService

@Service
class MerchantAuthService(
    private val merchantProfileRepository: MerchantProfileRepository,
    private val passwordService: PasswordService
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
                passwordHash = passwordService.hashPassword(password),
                merchantStatus = MerchantStatus.CLOSED
            )
        )
        return savedMerchant
    }

    fun getMerchantProfileByPhoneNumberAndValidatePassword(phoneNumber: String, password: String): MerchantProfile {
        val savedMerchant: MerchantProfile = merchantProfileRepository.findByPhoneNumber(phoneNumber) ?: 
            throw BadRequestException("Merchant with phone number ${phoneNumber} does not exist. Please sign up first.")
        if (!passwordService.isPasswordValid(
            inputPassword = password,
            storedHashedPassword = savedMerchant.passwordHash
        )) throw BadRequestException("Incorrect password for phone number ${phoneNumber}")
        return savedMerchant
    }
}