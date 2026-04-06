package com.ride.driver.backend.courier.controller

import java.net.URI
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseCookie
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.dto.AccessTokenDTO
import com.ride.driver.backend.courier.service.CourierAuthService
import com.ride.driver.backend.shared.auth.domain.JwtTokens

@RestController
@RequestMapping("api/v1/courier/auth")
class CourierAuthController (
    private val courierAuthService: CourierAuthService
){
private val logger: Logger = LoggerFactory.getLogger(CourierAuthController::class.java)

@PostMapping("/signup")
    fun courierSignup(@RequestBody @Valid req: CourierSignupDTO): ResponseEntity<AccessTokenDTO> {
        logger.info("event=courier_signup_request_received")
        val jwtTokens: JwtTokens = courierAuthService.signupCourier(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/login")
    fun courierLogin(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<AccessTokenDTO> {
        logger.info("event=courier_signup_request_received")
        val jwtTokens: JwtTokens = courierAuthService.loginCourier(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<AccessTokenDTO> {
        val jwtTokens: JwtTokens = courierAuthService.refreshToken(req)
        val cookieHeader: String = createCookie(jwtTokens.refreshToken)
        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, cookieHeader.toString())
                             .body(AccessTokenDTO(accessToken = jwtTokens.accessToken))
    }

    private fun createCookie(refreshToken: String): String {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/api/v1/courier/auth/refresh-token")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .build()
            .toString()
    }    
}