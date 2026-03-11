package com.ride.driver.backend.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.models.Coordinate
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.ConsumerProfileRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.courierProfile.VehicleType
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import com.ride.driver.backend.services.JwtTokenService
import com.ride.driver.backend.services.AccountRoles
import com.ride.driver.backend.services.AccessTokenData
import com.ride.driver.backend.services.AdditionalAccessTokenClaims
import com.ride.driver.backend.exceptions.BadRequestException
import java.util.UUID

// data class CourierSignInDTO(val courierName: String, val phoneNumber: String, val password: String)
data class CourierSignInDTO(
    val cpFirstName: String,
    val cpLastName: String,
    val phoneNumber: String,
    val password: String,
    val vehicleType: VehicleType,
)

data class CourierLoginDTO(
    val phoneNumber: String,
    val password: String
)

data class ConsumerSignInDTO(
    val cxFirstName: String,
    val cxLastName: String,
    val emailAddress: String,
    val password: String
)

data class JwtTokensDTO(val accessToken: String, val refreshToken: String? = null)

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
    private val repository: CourierProfileRepository,
    private val consumerProfileRepository: ConsumerProfileRepository
    ) {
    @PostMapping("/courier/signup")
    fun signup(@RequestBody @Valid req: CourierSignInDTO): ResponseEntity<JwtTokensDTO> {
        println("Signup request received: $req")
        val isCourierExists: Boolean = repository.existsByPhoneNumber(req.phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${req.phoneNumber} already exists")          
        val newCourierToRegister = CourierProfile(
            cpFirstName = req.cpFirstName,
            cpLastName = req.cpLastName,
            phoneNumber = req.phoneNumber,
            passwordHash = req.password.hashCode().toString(), // Simple hash for demonstration. Use a proper hashing algorithm in production.
            vehicleType = req.vehicleType,
            currentLocation = Coordinate(latitude = 0.0, longitude = 0.0), // Default location for new couriers
            cpStatus = CourierStatus.ONBOARDING
        )
        val savedCourier: CourierProfile = repository.save(newCourierToRegister)
        println("New courier registered with ID: ${savedCourier}")
        if (savedCourier.id == null) throw Exception("Failed to save new courier profile")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    accountID = savedCourier.id,
                    roles = listOf(AccountRoles.BASE_ROLE)
                ),
                accountName = savedCourier.cpFirstName + " " + savedCourier.cpLastName
            )
        )
        val refreshToken = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken))
    }

    @PostMapping("/courier/login")
    fun login(@RequestBody @Valid req: CourierLoginDTO): ResponseEntity<Pair<JwtTokensDTO, CourierSignInDTO>> {
        val savedCourier: CourierProfile = repository.findByPhoneNumber(req.phoneNumber) ?: 
            throw BadRequestException("Courier with phone number ${req.phoneNumber} does not exist. Please sign up first.")
        if (req.password.hashCode().toString() != savedCourier.passwordHash) throw BadRequestException("Incorrect password for phone number ${req.phoneNumber}")
        val accessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    accountID = savedCourier.id ?: throw Exception("Courier ID is null"),
                    roles = listOf(AccountRoles.BASE_ROLE)
                ),
                accountName = savedCourier.cpFirstName + " " +
                savedCourier.cpLastName
            )
        )
        val refreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(
            Pair(
                JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken),
                CourierSignInDTO(
                    cpFirstName = savedCourier.cpFirstName,
                    cpLastName = savedCourier.cpLastName,
                    phoneNumber = savedCourier.phoneNumber,
                    password = "Your password is securely stored and cannot be retrieved. If you forgot your password, please use the password reset option.", 
                    vehicleType = savedCourier.vehicleType
                )
            )
        )
    }

    @PostMapping("/consumer/signup")
    fun consumerSignup(@RequestBody @Valid req: ConsumerSignInDTO): ResponseEntity<String> {
        val isConsumerExists: Boolean = consumerProfileRepository.existsByEmailAddress(req.emailAddress)
        if (isConsumerExists) throw BadRequestException("Consumer with email address ${req.emailAddress} already exists")
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
            cxFirstName = req.cxFirstName,
            cxLastName = req.cxLastName,
            emailAddress = req.emailAddress,
            hashPassword = req.password.hashCode().toString() // Simple hash for demonstration. Use a proper hashing algorithm in production.
        ))
        println("New consumer registered with ID: ${savedConsumer}")
        if (savedConsumer.id == null) throw Exception("Failed to save new consumer profile")
        return ResponseEntity.ok("Consumer signup endpoint is under construction.")
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: JwtTokensDTO): ResponseEntity<JwtTokensDTO> {
        val courierId: UUID = jwtTokenService.extractAccountId(req.refreshToken
            ?: throw BadRequestException("Refresh token is required"))
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw BadRequestException("Invalid or expired refresh token")
        val savedCourier: CourierProfile = repository.findById(courierId)
            ?: throw BadRequestException("Courier with ID $courierId does not exist.")
        if (savedCourier.cpStatus == CourierStatus.SUSPENDED) throw BadRequestException("Courier account is suspended. Cannot refresh token.")        
         val newAccessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                additonalClaims = AdditionalAccessTokenClaims(
                    accountID = savedCourier.id ?: throw Exception("Courier ID is null"),
                    roles = listOf(AccountRoles.BASE_ROLE)
                ),
                accountName = savedCourier.cpFirstName + " " + savedCourier.cpLastName
            )
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(savedCourier.cpFirstName + " " + savedCourier.cpLastName)
        return ResponseEntity.ok(JwtTokensDTO(newAccessToken, newRefreshToken))
    }
}