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
import java.util.UUID

// data class CourierSignInDTO(val courierName: String, val phoneNumber: String, val password: String)
data class CourierSignInDTO(
    val cpFirstName: String,
    val cpLastName: String,
    val phoneNumber: String,
    val vehicleType: VehicleType,
)
data class JwtTokenResponseDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val repository: CourierProfileRepository
    ) {
    @PostMapping("/courier/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<JwtTokenResponseDTO> {
        println("Signup request received: $req")
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} already exists")          
        val newCourierToRegister = CourierProfile(
            phoneNumber = req.phoneNumber,
            passwordHash = req.password.hashCode().toString(),
            name = req.courierName,
            vehicleType = VehicleType.BIKE,
            status = CourierStatus.AVAILABLE
        )
        val savedCourier = repository.save(newCourierToRegister)
        println("New courier registered with ID: ${savedCourier}")
        if (savedCourier.id == null) throw Exception("Failed to save new courier profile")
        val accessToken = jwtTokenService.generateAccessToken(
            AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE), courierId = savedCourier.id?: throw Exception("Courier ID is null")),
            savedCourier.name
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.name)
        return ResponseEntity.ok(JwtTokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/courier/login")
    fun login(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<JwtTokenResponseDTO> {
        val savedCourier: CourierProfile = repository.findByPhoneNumber(req.phoneNumber) ?: 
            throw BadRequestException("Courier with phone number ${req.phoneNumber} does not exist. Please sign up first.")
        if (savedCourier.passwordHash !== req.password.hashCode().toString() && savedCourier.name !== req.courierName) 
            throw BadRequestException("Invalid credentials provided")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE), courierId = savedCourier.id?: throw Exception("Courier ID is null")),
            savedCourier.name
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.name)
        return ResponseEntity.ok(JwtTokenResponseDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid refreshToken: String): ResponseEntity<JwtTokenResponseDTO> {
        val courierId: UUID = jwtTokenService.extractCourierId(refreshToken)
        if (!jwtTokenService.isTokenValid(refreshToken)) throw BadRequestException("Invalid or expired refresh token")
        val savedCourier: CourierProfile = repository.findById(courierId)
            ?: throw BadRequestException("Courier with ID $courierId does not exist.")
        if (savedCourier.status == CourierStatus.SUSPENDED) throw BadRequestException("Courier account is suspended. Cannot refresh token.")        
         val newAccessToken: String = jwtTokenService.generateAccessToken(
            AdditionalAccessTokenClaims(roles = listOf(CourierRoles.BASE_ROLE), courierId = savedCourier.id?: throw Exception("Courier ID is null")),
            savedCourier.name
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.name)
        return ResponseEntity.ok(JwtTokenResponseDTO(newAccessToken, newRefreshToken))
    }
}