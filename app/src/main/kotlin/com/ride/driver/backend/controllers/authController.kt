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
import com.ride.driver.backend.exceptions.BadRequestException

data class CourierSignInDTO(val username: String, val phoneNumber: String, val password: String)
data class TokenResponseDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val repository: CourierProfileRepository
    ) {
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<TokenResponseDTO> {
        println("Signup request received: $req")
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} already exists")          
        val newCourier = CourierProfile(
            phoneNumber = req.phoneNumber,
            passwordHash = req.password.hashCode().toString(),
            name = req.username,
            vehicleType = VehicleType.BIKE,
            status = CourierStatus.AVAILABLE
        )
        val savedCourier = repository.save(newCourier)
        if (savedCourier.id == null) throw Exception("Failed to save new courier profile")
        val accessToken = jwtTokenService.generateAccessToken(
            AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE)),
            savedCourier.name
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.name)
        return ResponseEntity.ok(TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<TokenResponseDTO> {
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (!isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} does not exist")
        val username: String = req.username
        val accessToken: String = jwtTokenService.generateAccessToken(
            AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE)),
            username
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(username)
        return ResponseEntity.ok(TokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
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