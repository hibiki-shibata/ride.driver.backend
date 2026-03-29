package com.ride.driver.backend.courier.controller

import java.net.URI
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.courier.service.CourierAuthService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO

@RestController
@RequestMapping("/api/v1/couriers")
class CourierAuthController (
    private val courierAuthService: CourierAuthService
){
private val logger: Logger = LoggerFactory.getLogger(CourierAuthController::class.java)

@PostMapping("/auth/signup")
    fun courierSignup(@RequestBody @Valid req: CourierSignupDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=courier_signup_request_received")
        val jwtTokens: JwtTokensDTO = courierAuthService.signupCourier(req)
        return ResponseEntity
            .created(URI("/api/v1/couriers/me"))
            .body(jwtTokens)
    }

    @PostMapping("/auth/login")
    fun courierLogin(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<JwtTokensDTO> {
        logger.info("event=courier_signup_request_received")
        val jwtTokens: JwtTokensDTO = courierAuthService.loginCourier(req)
        return ResponseEntity.ok(jwtTokens)        
    }

    @PostMapping("auth/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<JwtTokensDTO> {
        val newJwtTokens: JwtTokensDTO = courierAuthService.refreshToken(req)
        return ResponseEntity.ok(newJwtTokens)
    }
}