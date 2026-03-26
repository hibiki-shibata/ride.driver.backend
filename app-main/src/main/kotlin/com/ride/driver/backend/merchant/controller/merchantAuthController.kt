package com.ride.driver.backend.merchant.controller

import java.net.URI
import java.util.UUID
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.model.MerchantProfile

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantAuthController(
    private val merchantAuthService: MerchantAuthService
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantAuthController::class.java)

    @PostMapping("/auth/signup")
    fun merchantSignup(@RequestBody @Valid req: MerchantSignupDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=merchant_signup_request_received")
        val jwtTokens: JwtTokensDTO = merchantAuthService.signupMerchant(req)   
        return ResponseEntity.created(
            URI("/api/v1/merchants/me")
            ).body(jwtTokens)
    }

    @PostMapping("/auth/login")
    fun merchantLogin(@RequestBody @Valid req: MerchantLoginDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=merchant_login_request_received")
        val jwtTokens: JwtTokensDTO = merchantAuthService.loginMerchant(req)
        return ResponseEntity.ok(jwtTokens)
    }
}