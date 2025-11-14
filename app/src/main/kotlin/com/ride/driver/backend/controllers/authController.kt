package com.ride.driver.backend.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.CourierStatus
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.CourierRoles
import com.ride.driver.backend.services.AdditionalAccessTokenClaims

data class CourierSignInDTO(val username: String, val phoneNumber: String, val password: String)
data class TokenResponseDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val repository: CourierProfileRepository
    ) {
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<String> {
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists !== true) {            
            val newCourier = CourierProfile(
                id = java.util.UUID.randomUUID(),
                phoneNumber = req.phoneNumber,
                passwordHash = req.password.hashCode().toString(),
                name = req.username,
                vehicleType = VehicleType.BIKE,
                status = CourierStatus.AVAILABLE
            )
            repository.save(newCourier) // Save the new courier profile in DB
            println("Registered new courier: $newCourier")
            return ResponseEntity.ok("${req.username} signed up successfully")
        } else {
            println("Signup failed: Courier with phone number ${req.phoneNumber} already exists")
            return ResponseEntity.status(409).body("Courier with phone number ${req.phoneNumber} already exists")
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<Any> {
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists !== true) {
            val username: String = req.username
            val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE))
            val accessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
            val refreshToken: String = jwtTokenService.generateRefreshToken(username)
            return ResponseEntity.ok(TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
        } else return ResponseEntity.status(401).body("Courier with phone number ${req.phoneNumber} not found")
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid refreshToken: String): ResponseEntity<TokenResponseDTO> {
        val username: String = jwtTokenService.extractUsername(refreshToken)
        val additionalAccessTokenClaims = AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE))
        val newAccessToken: String = jwtTokenService.generateAccessToken(additionalAccessTokenClaims, username)
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponseDTO(newAccessToken, newRefreshToken))
    }
}