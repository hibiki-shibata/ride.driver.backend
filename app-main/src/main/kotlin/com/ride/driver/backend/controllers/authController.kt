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
import com.ride.driver.backend.services.AccessTokenData
import com.ride.driver.backend.services.AdditionalAccessTokenClaims
import com.ride.driver.backend.exceptions.BadRequestException
import java.util.UUID

// data class CourierSignInDTO(val courierName: String, val phoneNumber: String, val password: String)
data class CourierBasicInfoDTO(
    val cpFirstName: String,
    val cpLastName: String,
    val phoneNumber: String,
    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    val phoneNumber: String
)

data class JwtTokensDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val repository: CourierProfileRepository
    ) {
    @PostMapping("/courier/signup")
    fun signup(@RequestBody @Valid req: CourierBasicInfoDTO): ResponseEntity<JwtTokensDTO> {
        println("Signup request received: $req")
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} already exists")          
        val newCourierToRegister = CourierProfile(
            cpFirstName = req.cpFirstName,
            cpLastName = req.cpLastName,
            phoneNumber = req.phoneNumber,
            vehicleType = req.vehicleType,
            cpStatus = CourierStatus.ONBOARDING
        )
        val savedCourier: CourierProfile = repository.save(newCourierToRegister)
        println("New courier registered with ID: ${savedCourier}")
        if (savedCourier.id == null) throw Exception("Failed to save new courier profile")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    courierId = savedCourier.id,
                    roles = listOf(CourierRoles.BASE_ROLE)
                ),
                courierName = savedCourier.cpFirstName + " " + savedCourier.cpLastName
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/courier/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<Pair<JwtTokensDTO, CourierBasicInfoDTO>> {
        val savedCourier: CourierProfile = repository.findByPhoneNumber(req.phoneNumber) ?: 
            throw BadRequestException("Courier with phone number ${req.phoneNumber} does not exist. Please sign up first.")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    courierId = savedCourier.id ?: throw Exception("Courier ID is null"),
                    roles = listOf(CourierRoles.BASE_ROLE)
                ),
                courierName = savedCourier.cpFirstName + " " +
                savedCourier.cpLastName
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(
            Pair(
                JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken),
                CourierBasicInfoDTO(
                    cpFirstName = savedCourier.cpFirstName,
                    cpLastName = savedCourier.cpLastName,
                    phoneNumber = savedCourier.phoneNumber,
                    vehicleType = savedCourier.vehicleType
                )
            )
        )
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: JwtTokensDTO): ResponseEntity<JwtTokensDTO> {
        val courierId: UUID = jwtTokenService.extractCourierId(req.refreshToken
            ?: throw BadRequestException("Refresh token is required"))
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw BadRequestException("Invalid or expired refresh token")
        val savedCourier: CourierProfile = repository.findById(courierId)
            ?: throw BadRequestException("Courier with ID $courierId does not exist.")
        if (savedCourier.cpStatus == CourierStatus.SUSPENDED) throw BadRequestException("Courier account is suspended. Cannot refresh token.")        
         val newAccessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    courierId = savedCourier.id ?: throw Exception("Courier ID is null"),
                    roles = listOf(CourierRoles.BASE_ROLE)
                ),
                courierName = savedCourier.cpFirstName + " " + savedCourier.cpLastName
            )
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(JwtTokensDTO(newAccessToken, newRefreshToken))
    }
}