package com.ride.driver.backend.consumer.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.net.URI
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
 
@RestController
@RequestMapping("/api/v1/consumers")
class ConsumerAuthController(
    private val consumerAuthService: ConsumerAuthService
) {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerAuthController::class.java)

    @PostMapping("/auth/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignupDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=consumer_signup_request_received")
        val jwtTokens: JwtTokensDTO = consumerAuthService.signupConsumer(req)
        return ResponseEntity.created(URI("/api/v1/consumers/me")).body(jwtTokens)
    }

    @PostMapping("/auth/login")
    fun consumerLogin(@RequestBody @Valid req: ConsumerLoginDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=consumer_login_request_received")
        val jwtTokens: JwtTokensDTO = consumerAuthService.loginConsumer(req)
        return ResponseEntity.ok(jwtTokens)
    }

    @PostMapping("auth/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<JwtTokensDTO> {
        val refreshedJwtTokens: JwtTokensDTO = consumerAuthService.refreshToken(req)
        return ResponseEntity.ok(refreshedJwtTokens)
    }
}