package com.ride.driver.backend.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.CourierRoles
import com.ride.driver.backend.services.AdditionalAccessTokenClaims
import com.ride.driver.backend.exceptions.AuthenticationException

data class CourierLoginDTO(val username: String, val password: String)
data class TokenResponseDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val jwtTokenService: JwtTokenService) {
    @PostMapping("/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<TokenResponseDTO> {
        val username: String = req.username
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE))
        val accessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val refreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestParam refreshToken: String): ResponseEntity<TokenResponseDTO> {
        val username: String = jwtTokenService.extractUsername(refreshToken)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE))
        val newAccessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponseDTO(newAccessToken, newRefreshToken))
    }
}