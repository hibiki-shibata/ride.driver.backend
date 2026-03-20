package com.ride.driver.backend.consumer.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO

@RestController
@RequestMapping("/api/v1/consumers")
class ConsumerAuthController(
    private val consumerAuthService: ConsumerAuthService,
    private val jwtTokenService: JwtTokenService
){
    @PostMapping("/auth/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignupDTO): ResponseEntity<JwtTokensDTO> {
        val savedConsumer: ConsumerProfile = consumerAuthService.registerNewConsumer(
            name = req.name,
            emailAddress = req.emailAddress,
            password = req.password,
            consumerAddress = req.consumerAddress,
            consumerAddressCoordinate = req.consumerAddressCoordinate
        )
        return ResponseEntity.ok(
            jwtTokenService.generateAccessTokenAndRefreshToken(
                AccessTokenData(
                    accountID = savedConsumer.id ?: throw Exception("Consumer ID is null"),
                    accountName = savedConsumer.name,
                    accountRoles = listOf(AccountRoles.BASE_ROLE)
                )
            )
        )
    }

    @PostMapping("/auth/login")
    fun consumerLogin(@RequestBody @Valid req: ConsumerLoginDTO): ResponseEntity<JwtTokensDTO> {
        val savedConsumer: ConsumerProfile = consumerAuthService.getConsumerProfileByEmailAdderessAndValidatePassword(
            emailAddress = req.emailAddress,
            password = req.password
        )
        return ResponseEntity.ok(
            jwtTokenService.generateAccessTokenAndRefreshToken(
                AccessTokenData(
                    accountID = savedConsumer.id ?: throw Exception("Consumer ID is null"),
                    accountName = savedConsumer.name,
                    accountRoles = listOf(AccountRoles.BASE_ROLE)
                )
            )
        )
    }
}