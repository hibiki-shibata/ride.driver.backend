package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate

import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException

import com.ride.driver.backend.courier.mapper.toAccessTokenClaim
import com.ride.driver.backend.courier.mapper.toCourierProfileResDTO
import com.ride.driver.backend.courier.mapper.toCourierProfileReqDTO
import com.ride.driver.backend.courier.mapper.toAccessTokenClaim
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Service
class CourierAuthService(
    private val courierProfileRepository: CourierProfileRepository,
    private val passwordService: PasswordService,
    private val jwtTokenService: JwtTokenService
) {
    private val logger: Logger = LoggerFactory.getLogger(CourierAuthService::class.java)
        
    fun signupCourier(req: CourierSignupDTO): JwtTokensDTO {
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
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedCourier.toAccessTokenClaim())
    }

    fun getCourierProfileByPhoneNumberAndValidatePassword(phoneNumber: String, password: String): CourierProfile {
        val savedCourier: CourierProfile = courierProfileRepository.findByPhoneNumber(phoneNumber) ?: 
            throw AccountNotFoundException("Courier with phone number ${phoneNumber} does not exist. Please sign up first.")
        if (!passwordService.isPasswordValid(
            inputPassword = password,
            storedHashedPassword = savedCourier.passwordHash
        )) throw IncorrectPasswordException("Incorrect password for phone number ${phoneNumber}")
        return savedCourier
    }
}