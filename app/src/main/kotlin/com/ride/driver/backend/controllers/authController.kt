package com.ride.driver.backend.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.Roles
import com.ride.driver.backend.services.AdditionalAccessTokenClaims

data class LoginRequest(val username: String, val password: String)
data class TokenResponse(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid req: LoginRequest): ResponseEntity<TokenResponse> {
        try{
        println("Authenticating user: ${req}")
        val username = req.username
        val password = req.password
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = defaultRoles, hashedPassword = password.hashCode().toString())
        val accessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val refreshToken: String = jwtTokenService.generateRefreshToken(username)
        println("Generated access token for user: $accessToken")
        println("Generated refresh token for user: $refreshToken")
        return ResponseEntity.ok(
            TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        ) 
        } catch (ex: Exception) {
            println("Authentication error: ${ex}")
            return ResponseEntity.status(4232).body(
                TokenResponse(
                    accessToken = "",
                    refreshToken = null
                )
            )
        }
    }

    @PostMapping("/refresh")
    fun refresh(@RequestParam refreshToken: String): ResponseEntity<TokenResponse> {
        val username = jwtTokenService.extractUsername(refreshToken)
        val password = jwtTokenService.extractUserHashedPassword(refreshToken)
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = defaultRoles, hashedPassword = password.hashCode().toString())
        val newAccessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
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
