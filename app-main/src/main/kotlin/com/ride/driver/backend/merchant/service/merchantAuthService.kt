package com.ride.driver.backend.merchant.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.mapper.toAccessTokenClaim
import com.ride.driver.backend.merchant.mapper.toRefreshTokenClaim
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.shared.model.Coordinate

@Service
class MerchantAuthService(
    private val merchantProfileRepository: MerchantProfileRepository,
    private val passwordService: PasswordService,
    private val jwtTokenService: JwtTokenService
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantAuthService::class.java)
    
    @Transactional
    fun signupMerchant(
        req: MerchantSignupDTO         
    ): JwtTokensDTO {
        if (merchantProfileRepository.existsByPhoneNumber(req.phoneNumber))
             throw AccountConflictException("Merchant with phone number already exists")
        val savedMerchant: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = req.name,
                phoneNumber = req.phoneNumber,
                merchantAddress = req.merchantAddress,
                merchantAddressCoordinate = req.merchantAddressCoordinate,
                passwordHash = passwordService.hashPassword(req.password),
                merchantStatus = MerchantStatus.ADMINS_ONLY
            )
        )
        logger.info("event=merchant_signup_completed consumerId={}", savedMerchant.id)
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = savedMerchant.toAccessTokenClaim(),
            refreshTokenClaim = savedMerchant.toRefreshTokenClaim()
         )
    }

    fun loginMerchant(
        req: MerchantLoginDTO
    ): JwtTokensDTO {
        val savedMerchant: MerchantProfile = merchantProfileRepository.findByPhoneNumber(req.phoneNumber) ?: 
            throw AccountNotFoundException("Merchant with the phone number does not exist. Please sign up first.")
        val isPasswordValid: Boolean = passwordService.isPasswordValid(
            inputPassword = req.password,
            storedHashedPassword = savedMerchant.passwordHash
        )        
        if (!isPasswordValid) throw IncorrectPasswordException("Incorrect password")
        logger.info("event=merchant_login_completed consumerId={}", savedMerchant.id)
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = savedMerchant.toAccessTokenClaim(),
            refreshTokenClaim = savedMerchant.toRefreshTokenClaim()
         )
    }

    fun refreshToken(
        req: TokenRefreshDTO,
    ): JwtTokensDTO{
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
        val accountDetails: RefreshTokenClaim = jwtTokenService.extractRefreshTokenClaim(req.refreshToken)
        val merchantProfile: MerchantProfile = merchantProfileRepository.findById(accountDetails.accountId).orElseThrow {
             InvalidJwtTokenException("Merchant not found for the given token")
        }
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = merchantProfile.toAccessTokenClaim(),
            refreshTokenClaim = merchantProfile.toRefreshTokenClaim()
        )
    }
}