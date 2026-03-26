package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.service.PasswordService

import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class MerchantAuthService(
    private val merchantProfileRepository: MerchantProfileRepository,
    private val passwordService: PasswordService
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantAuthService::class.java)
    
    fun signupMerchant(
        req: MerchantSignupDTO         
    ): MerchantProfile {
        if (merchantProfileRepository.existsByPhoneNumber(req.phoneNumber))
             throw AccountConflictException("Merchant with phone number already exists")
        val savedMerchant: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = name,
                phoneNumber = req.phoneNumber,
                merchantAddress = req.merchantAddress,
                merchantAddressCoordinate = req.merchantAddressCoordinate,
                passwordHash = passwordService.hashPassword(req.password),
                merchantStatus = MerchantStatus.ADMINS_ONLY
            )
        )
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedMerchant.toAccessTokenClaim())
    }

    fun loginMerchant(phoneNumber: String, password: String): MerchantProfile {
        val savedMerchant: MerchantProfile = merchantProfileRepository.findByPhoneNumber(phoneNumber) ?: 
            throw AccountNotFoundException("Merchant with phone number ${phoneNumber} does not exist. Please sign up first.")
        if (!passwordService.isPasswordValid(
            inputPassword = password,
            storedHashedPassword = savedMerchant.passwordHash
        )) throw IncorrectPasswordException("Incorrect password for phone number ${phoneNumber}")
        return savedMerchant
    }
}