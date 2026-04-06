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
import org.springframework.http.ResponseCookie
import org.springframework.http.HttpHeaders
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.dto.AccessTokenDTO
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.dto.MerchantLoginDTO

@RestController
@RequestMapping("api/v1/merchant/auth")
class MerchantAuthController(
    private val merchantAuthService: MerchantAuthService
) {
    private val logger: Logger = LoggerFactory.getLogger(MerchantAuthController::class.java)

    @PostMapping("/signup")
    fun merchantSignup(@RequestBody @Valid req: MerchantSignupDTO): ResponseEntity<AccessTokenDTO> {
        logger.info("event=merchant_signup_request_received")
        val jwtTokens: JwtTokens = merchantAuthService.signupMerchant(req) 
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/login")
    fun merchantLogin(@RequestBody @Valid req: MerchantLoginDTO): ResponseEntity<AccessTokenDTO> {
        logger.info("event=merchant_login_request_received")
        val jwtTokens: JwtTokens = merchantAuthService.loginMerchant(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<AccessTokenDTO> {
        val jwtTokens: JwtTokens = merchantAuthService.refreshToken(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    private fun createCookie(refreshToken: String): String {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/merchant/auth/refresh-token")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .build()
            .toString()
    }    
}