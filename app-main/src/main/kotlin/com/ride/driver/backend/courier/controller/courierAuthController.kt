package com.ride.driver.backend.courier.controller

import java.net.URI
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.courier.service.CourierAuthService
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.AccountNotFoundException

@RestController
@RequestMapping("/api/v1/couriers")
class CourierAuthController (
    private val courierAuthService: CourierAuthService,
    private val jwtTokenService: JwtTokenService

){
@PostMapping("/auth/signup")
    fun courierSignup(@RequestBody @Valid req: CourierSignupDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierAuthService.registerNewCourier(
            phoneNumber = req.phoneNumber,
            password = req.password,
            name = req.name,
            vehicleType = req.vehicleType
        )
        return ResponseEntity.created(URI("/api/v1/couriers/${savedCourier.id}")).body(
            jwtTokenService.generateAccessTokenAndRefreshToken(
                AccessTokenClaim(
                    accountID = savedCourier.id ?: throw AccountNotFoundException("Courier ID is null"),
                    accountName = savedCourier.name,
                    accountRoles = listOf(AccountRoles.BASE_COURIER_ROLE)
                )
            )
        )
    }

    @PostMapping("/auth/login")
    fun courierLogin(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedCourier: CourierProfile = courierAuthService.getCourierProfileByPhoneNumberAndValidatePassword(
            phoneNumber = req.phoneNumber,
            password = req.password
        )
        return ResponseEntity.ok(
            jwtTokenService.generateAccessTokenAndRefreshToken(
                AccessTokenClaim(
                    accountID = savedCourier.id ?: throw AccountNotFoundException("Courier ID is null"),
                    accountName = savedCourier.name,
                    accountRoles = listOf(AccountRoles.BASE_COURIER_ROLE)
                )
            )
        )
    }
}