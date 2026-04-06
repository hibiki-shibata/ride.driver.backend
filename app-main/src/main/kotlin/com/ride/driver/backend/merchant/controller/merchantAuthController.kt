package com.ride.driver.backend.merchant.controller

import java.net.URI
import java.util.UUID
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.dto.MerchantLoginDTO

@RestController
@RequestMapping("api/v1/merchants/auth")
class MerchantAuthController(
    private val merchantAuthService: MerchantAuthService
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantAuthController::class.java)

    @PostMapping("/signup")
    fun merchantSignup(@RequestBody @Valid req: MerchantSignupDTO): ResponseEntity<JwtTokens> {
        logger.info("event=merchant_signup_request_received")
        val jwtTokens: JwtTokens = merchantAuthService.signupMerchant(req)   
        return ResponseEntity.created(
            URI("/api/v1/merchants/me")
            ).body(jwtTokens)
    }

    @PostMapping("/login")
    fun merchantLogin(@RequestBody @Valid req: MerchantLoginDTO): ResponseEntity<JwtTokens> {
        logger.info("event=merchant_login_request_received")
        val jwtTokens: JwtTokens = merchantAuthService.loginMerchant(req)
        return ResponseEntity.ok(jwtTokens)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<JwtTokens> {
        val newJwtTokens: JwtTokens = merchantAuthService.refreshToken(req)
        return ResponseEntity.ok(newJwtTokens)
    }
}