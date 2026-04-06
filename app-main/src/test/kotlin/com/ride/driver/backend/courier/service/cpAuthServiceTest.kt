package com.ride.driver.backend.courier.service

import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.Optional
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CourierAuthServiceTest {

    private lateinit var courierProfileRepository: CourierProfileRepository
    private lateinit var passwordService: PasswordService
    private lateinit var jwtTokenService: JwtTokenService
    private lateinit var courierAuthService: CourierAuthService

    @BeforeEach
    fun setUp() {
        courierProfileRepository = mockk()
        passwordService = mockk()
        jwtTokenService = mockk()
        courierAuthService = CourierAuthService(
            courierProfileRepository = courierProfileRepository,
            passwordService = passwordService,
            jwtTokenService = jwtTokenService
        )
    }

    @Test
    fun `signupCourier should throw AccountConflictException when phone number already exists`() {
        val req = mockk<CourierSignupDTO>()
        every { req.phoneNumber } returns "+123456789"

        every { courierProfileRepository.existsByPhoneNumber(any()) } returns true

        val thrown = assertThrows(AccountConflictException::class.java) {
            courierAuthService.signupCourier(req)
        }

        assertEquals("Courier with request phone number already exists", thrown.message)

        verify(exactly = 1) { courierProfileRepository.existsByPhoneNumber(any()) }
        verify(exactly = 0) { passwordService.hashPassword(any()) }
        verify(exactly = 0) { courierProfileRepository.save(any()) }
        verify(exactly = 0) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(courierProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `signupCourier should hash password save courier and return jwt tokens`() {
        val req = mockk<CourierSignupDTO>()
        val jwtTokens = mockk<JwtTokens>()
        val vehicleType = enumValues<VehicleType>().first()
        val courierId = UUID.randomUUID()

        val savedCourier = buildCourierProfile(
            id = courierId,
            name = "John Driver",
            phoneNumber = "+123456789",
            passwordHash = "hashed-password",
            vehicleType = vehicleType
        )

        every { req.name } returns "John Driver"
        every { req.phoneNumber } returns "+123456789"
        every { req.password } returns "plain-password"
        every { req.vehicleType } returns vehicleType

        every { courierProfileRepository.existsByPhoneNumber(any()) } returns false
        every { passwordService.hashPassword(any()) } returns "hashed-password"

        val courierSlot = slot<CourierProfile>()
        every { courierProfileRepository.save(any()) } returns savedCourier
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = courierAuthService.signupCourier(req)

        assertSame(jwtTokens, result)
    }

    @Test
    fun `loginCourier should throw AccountNotFoundException when courier does not exist`() {
        val req = mockk<CourierLoginDTO>()

        every { req.phoneNumber } returns "+123456789"
        every { courierProfileRepository.findByPhoneNumber(any()) } returns null

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            courierAuthService.loginCourier(req)
        }

        assertEquals(
            "Courier with the phone number does not exist. Please sign up first.",
            thrown.message
        )

        verify(exactly = 1) { courierProfileRepository.findByPhoneNumber(any()) }
        verify(exactly = 0) {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = any()
            )
        }
        verify(exactly = 0) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(courierProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `loginCourier should throw IncorrectPasswordException when password is invalid`() {
        val req = mockk<CourierLoginDTO>()
        val savedCourier = buildCourierProfile(
            id = UUID.randomUUID(),
            name = "Jane Driver",
            phoneNumber = "+198765432",
            passwordHash = "stored-hash",
            vehicleType = enumValues<VehicleType>().first()
        )

        every { req.phoneNumber } returns "+198765432"
        every { req.password } returns "wrong-password"
        every { courierProfileRepository.findByPhoneNumber(any()) } returns savedCourier
        every {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = savedCourier.passwordHash
            )
        } returns false

        val thrown = assertThrows(IncorrectPasswordException::class.java) {
            courierAuthService.loginCourier(req)
        }

        assertEquals("Incorrect password provided", thrown.message)

        verify(exactly = 1) { courierProfileRepository.findByPhoneNumber(any()) }
        verify(exactly = 1) {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = savedCourier.passwordHash
            )
        }
        verify(exactly = 0) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(courierProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `loginCourier should return jwt tokens when credentials are valid`() {
        val req = mockk<CourierLoginDTO>()
        val jwtTokens = mockk<JwtTokens>()
        val savedCourier = buildCourierProfile(
            id = UUID.randomUUID(),
            name = "Valid Courier",
            phoneNumber = "+111222333",
            passwordHash = "stored-hash",
            vehicleType = enumValues<VehicleType>().first()
        )

        every { req.phoneNumber } returns "+111222333"
        every { req.password } returns "correct-password"
        every { courierProfileRepository.findByPhoneNumber(any()) } returns savedCourier
        every {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = savedCourier.passwordHash
            )
        } returns true
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = courierAuthService.loginCourier(req)

        assertSame(jwtTokens, result)

        verify(exactly = 1) { courierProfileRepository.findByPhoneNumber(any()) }
        verify(exactly = 1) {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = savedCourier.passwordHash
            )
        }
        verify(exactly = 1) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(courierProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `refreshToken should throw InvalidJwtTokenException when courier is not found`() {
        val req = mockk<TokenRefreshDTO>()
        val refreshTokenClaim = mockk<RefreshTokenClaim>()
        val courierId = UUID.randomUUID()

        every { req.refreshToken } returns "refresh-token"
        every { refreshTokenClaim.accountId } returns courierId
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = ServiceType.COURIER
            )
        } returns refreshTokenClaim
        every { courierProfileRepository.findById(refreshTokenClaim.accountId) } returns Optional.empty()

        val thrown = assertThrows(InvalidJwtTokenException::class.java) {
            courierAuthService.refreshToken(req)
        }

        assertEquals("Courier not found for the given token", thrown.message)
    }

    @Test
    fun `refreshToken should return new jwt tokens when token is valid`() {
        val req = mockk<TokenRefreshDTO>()
        val refreshTokenClaim = mockk<RefreshTokenClaim>()
        val jwtTokens = mockk<JwtTokens>()
        val courierId = UUID.randomUUID()

        val savedCourier = buildCourierProfile(
            id = courierId,
            name = "Refresh Courier",
            phoneNumber = "+444555666",
            passwordHash = "stored-hash",
            vehicleType = enumValues<VehicleType>().first()
        )

        every { req.refreshToken } returns "valid-refresh-token"
        every { refreshTokenClaim.accountId } returns courierId
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = ServiceType.COURIER
            )
        } returns refreshTokenClaim
        every { courierProfileRepository.findById(any()) } returns Optional.of(savedCourier)
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = courierAuthService.refreshToken(req)

        assertSame(jwtTokens, result)
    }

    private fun buildCourierProfile(
        id: UUID,
        name: String,
        phoneNumber: String,
        passwordHash: String,
        vehicleType: VehicleType
    ): CourierProfile {
        return CourierProfile(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            passwordHash = passwordHash,
            vehicleType = vehicleType,
            currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
            cpStatus = CourierStatus.ONBOARDING
        )
    }
}