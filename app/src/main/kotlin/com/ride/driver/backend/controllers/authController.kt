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

data class LoginRequestDTO(val username: String, val password: String)
data class TokenResponseDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService
) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid req: LoginRequestDTO): ResponseEntity<TokenResponseDTO> {
        println("Authenticating user: ${req}")
        val username = req.username
        val password = req.password
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = defaultRoles, hashedPassword = password.hashCode().toString())
        val accessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val refreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(
            TokenResponseDTO(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        ) 
    }

    @PostMapping("/refresh")
    fun refresh(@RequestParam refreshToken: String): ResponseEntity<TokenResponseDTO> {
        val username = jwtTokenService.extractUsername(refreshToken)
        val password = jwtTokenService.extractUserHashedPassword(refreshToken)
        val defaultRoles = listOf(Roles.BASE_ROLE)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = defaultRoles, hashedPassword = password.hashCode().toString())
        val newAccessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponseDTO(newAccessToken, newRefreshToken))
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
