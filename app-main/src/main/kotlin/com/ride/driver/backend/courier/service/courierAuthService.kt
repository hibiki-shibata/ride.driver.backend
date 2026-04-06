package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.courier.mapper.toCourierProfileResDto
import com.ride.driver.backend.courier.mapper.toTokenClaims
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

@Service
class CourierAuthService(
    private val courierProfileRepository: CourierProfileRepository,
    private val passwordService: PasswordService,
    private val jwtTokenService: JwtTokenService
) {
    private val logger: Logger = LoggerFactory.getLogger(CourierAuthService::class.java)
    
    @Transactional
    fun signupCourier(req: CourierSignupDTO): JwtTokens {
        if (courierProfileRepository.existsByPhoneNumber(req.phoneNumber))
            throw AccountConflictException("Courier with request phone number already exists")
        
        val savedCourier: CourierProfile = courierProfileRepository.save(
                CourierProfile(
                name = req.name,
                phoneNumber = req.phoneNumber,
                passwordHash = passwordService.hashPassword(req.password),
                vehicleType = req.vehicleType,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0), // Default location for new couriers
                cpStatus = CourierStatus.ONBOARDING
            )
        )
        logger.info("event=courier_signup_completed courierId={}", savedCourier.id)     
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedCourier.toTokenClaims())
    }

    fun loginCourier(req: CourierLoginDTO): JwtTokens {
        val savedCourier: CourierProfile = courierProfileRepository.findByPhoneNumber(req.phoneNumber) ?: 
            throw AccountNotFoundException("Courier with the phone number does not exist. Please sign up first.")
            val isPasswordValid: Boolean = passwordService.isPasswordValid(
                inputPassword = req.password,
                storedHashedPassword = savedCourier.passwordHash
            )
        if (!isPasswordValid) throw IncorrectPasswordException("Incorrect password provided")
        logger.info("event=courier_login_completed courierId={}", savedCourier.id)     
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedCourier.toTokenClaims())
    }

    fun refreshToken(
        refreshToken: String
    ): JwtTokens{
        val accountDetails: RefreshTokenClaim = jwtTokenService.extractRefreshTokenClaimAndValidate(
            token = refreshToken,
            expectedServiceType = ServiceType.COURIER
        )
        val savedCourier: CourierProfile = courierProfileRepository.findById(accountDetails.accountId).orElseThrow {
             InvalidJwtTokenException("Courier not found for the given token")
        }
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedCourier.toTokenClaims())
    }
}