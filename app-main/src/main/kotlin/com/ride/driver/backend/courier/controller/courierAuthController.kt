package com.ride.driver.backend.courier.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.courier.dto.CourierSignInDTO
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.courier.service.CourierAuthService
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.service.PasswordService

@RestController
@RequestMapping("/api/v1/couriers")
class CourierAuthController (
    private val courierAuthService: CourierAuthService,
    private val jwtTokenService: JwtTokenService

){
@PostMapping("/auth/signup")
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

    @PostMapping("/auth/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierAuthService.getCourierProfileByPhoneNumberAndValidatePassword(
            phoneNumber = req.phoneNumber,
            password = req.password
        )
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

}