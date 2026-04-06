package com.ride.driver.backend.consumer.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseCookie
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.CookieValue
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
import com.ride.driver.backend.shared.auth.dto.AccessTokenDTO
import com.ride.driver.backend.shared.auth.domain.JwtTokens
 
@RestController
@RequestMapping("api/v1/consumer/auth")
class ConsumerAuthController(
    private val consumerAuthService: ConsumerAuthService,
) {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerAuthController::class.java)

    @PostMapping("/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignupDTO): ResponseEntity<AccessTokenDTO> {
        logger.info("event=consumer_signup_request_received")
        val jwtTokens: JwtTokens = consumerAuthService.signupConsumer(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader)
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/login")
    fun consumerLogin(@RequestBody @Valid req: ConsumerLoginDTO): ResponseEntity<AccessTokenDTO>{
        logger.info("event=consumer_login_request_received")
        val jwtTokens: JwtTokens = consumerAuthService.loginConsumer(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@CookieValue("refreshToken") refreshToken: String): ResponseEntity<AccessTokenDTO> {
        logger.info("event=consumer_refresh_token_request_received")
        val jwtTokens: JwtTokens = consumerAuthService.refreshToken(refreshToken)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    private fun createCookie(refreshToken: String): String {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/consumer/auth/refresh-token")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .build()
            .toString()
    }
}