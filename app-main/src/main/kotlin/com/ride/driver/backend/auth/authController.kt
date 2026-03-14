package com.ride.driver.backend.auth.controllers

import java.util.UUID
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.merchant.models.MerchantStatus
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.auth.services.JwtTokenService
import com.ride.driver.backend.auth.dto.*
import com.ride.driver.backend.auth.domain.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val courierProfileRepository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val merchantProfileRepository: MerchantProfileRepository
) {
    @PostMapping("/courier/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<JwtTokensDTO> {
        val isCourierExists: Boolean = courierProfileRepository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} already exists")          
        val newCourierToRegister = CourierProfile(
            name = req.name,
            phoneNumber = req.phoneNumber,
            passwordHash = req.password.hashCode().toString(), // Simple hash for demonstration. Use a proper hashing algorithm in production.
            vehicleType = req.vehicleType,
            currentLocation = Coordinate(latitude = 0.0, longitude = 0.0), // Default location for new couriers
            cpStatus = CourierStatus.ONBOARDING
        )
        val savedCourier: CourierProfile = courierProfileRepository.save(newCourierToRegister)
        if (savedCourier.id == null) throw Exception("Failed to save new courier profile")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedCourier.id,
                accountName = savedCourier.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/courier/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierProfileRepository.findByPhoneNumber(req.phoneNumber) ?: 
            throw BadRequestException("Courier with phone number ${req.phoneNumber} does not exist. Please sign up first.")
        if (req.password.hashCode().toString() != savedCourier.passwordHash) throw BadRequestException("Incorrect password for phone number ${req.phoneNumber}")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedCourier.id ?: throw Exception("Courier ID is null"),
                accountName = savedCourier.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/consumer/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignInDTO): ResponseEntity<JwtTokensDTO> {
        val isConsumerExists: Boolean = consumerProfileRepository.existsByEmailAddress(req.emailAddress)
        if (isConsumerExists) throw BadRequestException("Consumer with email address ${req.emailAddress} already exists")
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
            name = req.name,
            emailAddress = req.emailAddress,
            homeAddress = req.homeAddress,
            homeAddressCoordinate = req.homeAddressCoordinate,
            passwordHash = req.password.hashCode().toString() // Simple hash for demonstration. Use a proper hashing algorithm in production.
        ))
        if (savedConsumer.id == null) throw Exception("Failed to save new consumer profile")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedConsumer.id ?: throw Exception("Consumer ID is null"),
                accountName = savedConsumer.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedConsumer.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/consumer/login")
    fun consumerLogin(@RequestBody @Valid req: ConsumerLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedConsumer: ConsumerProfile = consumerProfileRepository.findByEmailAddress(req.emailAddress) ?: 
            throw BadRequestException("Consumer with email address ${req.emailAddress} does not exist. Please sign up first.")
        if (req.password.hashCode().toString() != savedConsumer.passwordHash) throw BadRequestException("Incorrect password for email address ${req.emailAddress}")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedConsumer.id ?: throw Exception("Consumer ID is null"),
                accountName = savedConsumer.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedConsumer.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/merchant/signup")
    fun merchantSignup(@RequestBody @Valid req: MerchantSignInDTO): ResponseEntity<JwtTokensDTO> {
        val isMerchantExists: Boolean = merchantProfileRepository.existsByPhoneNumber(req.phoneNumber)
        if (isMerchantExists) throw BadRequestException("Merchant with phone number ${req.phoneNumber} already exists")
        val savedMerchant: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = req.name,
                phoneNumber = req.phoneNumber,
                merchantAddress = req.merchantAddress,
                merchantAddressCoordiate = req.merchantAddressCoordinate,
                passwordHash = req.password.hashCode().toString(), // Simple hash for demonstration. Use a proper hashing algorithm in production.
                merchantStatus = MerchantStatus.CLOSED
            )
        )
        if (savedMerchant.id == null) throw Exception("Failed to save new merchant profile")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedMerchant.id ?: throw Exception("Merchant ID is null"),
                accountName = savedMerchant.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedMerchant.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))        
    }

    @PostMapping("/merchant/login")
    fun merchantLogin(@RequestBody @Valid req: MerchantLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedMerchant: MerchantProfile = merchantProfileRepository.findByPhoneNumber(req.phoneNumber) ?: 
            throw BadRequestException("Merchant with phone number ${req.phoneNumber} does not exist. Please sign up first.")
        if (req.password.hashCode().toString() != savedMerchant.passwordHash) throw BadRequestException("Incorrect password for phone number ${req.phoneNumber}")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedMerchant.id ?: throw Exception("Merchant ID is null"),
                accountName = savedMerchant.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedMerchant.id)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: JwtTokensDTO): ResponseEntity<JwtTokensDTO> {
        val accountIdOfRefreshToken: UUID = jwtTokenService.extractAccountId(req.refreshToken)
        val acccountIdOfAccessToken: UUID = jwtTokenService.extractAccountId(req.accessToken)
        if (accountIdOfRefreshToken != acccountIdOfAccessToken) throw BadRequestException("Account ID in refresh token does not match account ID in access token")
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw BadRequestException("Invalid or expired refresh token")
        val accountName: String = jwtTokenService.extractAccountName(req.accessToken)
        val newAccessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = accountIdOfRefreshToken,
                accountName = accountName,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(accountIdOfRefreshToken)
        return ResponseEntity.ok(JwtTokensDTO(newAccessToken, newRefreshToken))
    }
}