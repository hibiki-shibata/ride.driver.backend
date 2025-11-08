package com.ride.driver.backend.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.Roles
import com.ride.driver.backend.services.AdditionalJwtTokenClaims

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid req: LoginRequest): ResponseEntity<TokenResponse> {
        val username = req.username
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalJwtTokenClaims = AdditionalJwtTokenClaims(roles = defaultRoles)
        val accessToken: String = jwtTokenService.generateAccessToken(additionalJwtTokenClaims, username)
        val refreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponse(accessToken, refreshToken))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestParam refreshToken: String): ResponseEntity<TokenResponse> {
        val username = jwtTokenService.extractUsername(refreshToken)
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalJwtTokenClaims = AdditionalJwtTokenClaims(roles = defaultRoles)
        val newAccessToken: String = jwtTokenService.generateAccessToken(additionalJwtTokenClaims, username)
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponse(newAccessToken, newRefreshToken))
    }
}

// package com.ride.driver.backend.controller

// @RestController
// @RequestMapping("/api/v1/auth")
// class AuthController(
//     private val authenticationService: AuthenticationService
// ) {
//     @PostMapping
//     fun authenticate(
//       @RequestBody authRequest: AuthenticationRequest
//     ): AuthenticationResponse =
//         authenticationService.authentication(authRequest)

//     @PostMapping("/refresh")
//     fun refreshAccessToken(
//       @RequestBody request: RefreshTokenRequest
//     ): TokenResponse = TokenResponse(token = authenticationService.refreshAccessToken(request.token))
// }
