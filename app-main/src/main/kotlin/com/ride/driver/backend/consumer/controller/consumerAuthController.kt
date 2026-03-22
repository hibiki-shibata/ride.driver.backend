package com.ride.driver.backend.consumer.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import org.slf4j.LoggerFactory
import org.slf4j.Logger
 
@RestController
@RequestMapping("/api/v1/consumers")
class ConsumerAuthController(
    private val consumerAuthService: ConsumerAuthService,
    private val jwtTokenService: JwtTokenService
) {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerAuthController::class.java)

    @PostMapping("/auth/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignupDTO): ResponseEntity<JwtTokensDTO> {
        val savedConsumer: ConsumerProfile = consumerAuthService.registerNewConsumer(
            name = req.name,
            emailAddress = req.emailAddress,
            password = req.password,
            consumerAddress = req.consumerAddress,
            consumerAddressCoordinate = req.consumerAddressCoordinate
        )
        logger.info("New consumer registered: ${savedConsumer.name} with email ${savedConsumer.emailAddress}")
        logger.debug("Consumer details: ID=${savedConsumer.id}, Address=${savedConsumer.consumerAddress}, Coordinate=${savedConsumer.consumerAddressCoordinate}")
        logger.warn("Ensure that sensitive information is not logged in production environments")
        logger.error("This is a test error log for consumer signup - remove in production")
        return ResponseEntity.created(URI("/api/v1/consumers/${savedConsumer.id}")).body(
            jwtTokenService.generateAccessTokenAndRefreshToken(
                AccessTokenData(
                    accountID = savedConsumer.id ?: throw AccountNotFoundException("Consumer ID is null"),
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
                    accountID = savedConsumer.id ?: throw AccountNotFoundException("Consumer ID is null"),
                    accountName = savedConsumer.name,
                    accountRoles = listOf(AccountRoles.BASE_ROLE)
                )
            )
        )
    }
}