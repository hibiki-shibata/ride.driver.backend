package com.ride.driver.backend.auth.controllers

import java.util.UUID
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.auth.services.JwtTokenService
import com.ride.driver.backend.auth.dto.*
import com.ride.driver.backend.auth.domain.*
import com.ride.driver.backend.auth.services.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val courierAuthService: CourierAuthService,
    private val consumerAuthService: ConsumerAuthService,
    private val merchantAuthService: MerchantAuthService,
    private val jwtTokenService: JwtTokenService,
) {
    private fun isPasswordValid(inputPassword: String, storedPasswordHash: String): Boolean {
        return inputPassword.hashCode().toString() == storedPasswordHash        
    } 
    
    @PostMapping("/courier/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierAuthService.registerNewCourier(
            phoneNumber = req.phoneNumber,
            password = req.password,
            name = req.name,
            vehicleType = req.vehicleType
        )
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = savedCourier.id ?: throw Exception("Courier ID is null"),
                accountName = savedCourier.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.id ?: throw Exception("Courier ID is null"))
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/courier/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierAuthService.getCourierProfileByPhoneNumber(req.phoneNumber)
        if (!isPasswordValid(req.password, savedCourier.passwordHash)) throw BadRequestException("Incorrect password for phone number ${req.phoneNumber}")
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
        val savedConsumer: ConsumerProfile = consumerAuthService.registerNewConsumer(
            name = req.name,
            emailAddress = req.emailAddress,
            password = req.password,
            homeAddress = req.homeAddress,
            homeAddressCoordinate = req.homeAddressCoordinate
        )   
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
        val savedConsumer: ConsumerProfile = consumerAuthService.getConsumerProfileByEmailAddress(req.emailAddress)
        if (!isPasswordValid(req.password, savedConsumer.passwordHash)) throw BadRequestException("Incorrect password for email address ${req.emailAddress}")
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
        val savedMerchant: MerchantProfile = merchantAuthService.registerNewMerchant(
            name = req.name,
            phoneNumber = req.phoneNumber,
            password = req.password,
            merchantAddress = req.merchantAddress,
            merchantAddressCoordinate = req.merchantAddressCoordinate
        )
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
        val savedMerchant: MerchantProfile = merchantAuthService.getMerchantProfileByPhoneNumber(req.phoneNumber)
        if (!isPasswordValid(req.password, savedMerchant.passwordHash)) throw BadRequestException("Incorrect password for phone number ${req.phoneNumber}")
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