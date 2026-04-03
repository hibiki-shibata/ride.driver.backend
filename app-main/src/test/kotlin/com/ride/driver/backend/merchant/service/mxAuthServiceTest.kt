package com.ride.driver.backend.merchant.service

import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
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

class MerchantAuthServiceTest {

    private lateinit var merchantProfileRepository: MerchantProfileRepository
    private lateinit var passwordService: PasswordService
    private lateinit var jwtTokenService: JwtTokenService
    private lateinit var merchantAuthService: MerchantAuthService

    @BeforeEach
    fun setUp() {
        merchantProfileRepository = mockk()
        passwordService = mockk()
        jwtTokenService = mockk()
        merchantAuthService = MerchantAuthService(
            merchantProfileRepository = merchantProfileRepository,
            passwordService = passwordService,
            jwtTokenService = jwtTokenService
        )
    }

    @Test
    fun `signupMerchant should throw AccountConflictException when phone number already exists`() {
        val req = mockk<MerchantSignupDTO>()

        every { req.phoneNumber } returns "+123456789"
        every { merchantProfileRepository.existsByPhoneNumber(any()) } returns true

        val thrown = assertThrows(AccountConflictException::class.java) {
            merchantAuthService.signupMerchant(req)
        }

        assertEquals("Merchant with phone number already exists", thrown.message)
    }

    @Test
    fun `signupMerchant should hash password save merchant and return jwt tokens`() {
        val req = mockk<MerchantSignupDTO>()
        val jwtTokens = mockk<JwtTokensDTO>()
        val merchantId = UUID.randomUUID()
        val merchantCoordinate = Coordinate(latitude = 35.6812, longitude = 139.7671)

        val savedMerchant = buildMerchantProfile(
            id = merchantId,
            name = "Test Merchant",
            phoneNumber = "+123456789",
            merchantAddress = "Tokyo Station",
            merchantAddressCoordinate = merchantCoordinate,
            passwordHash = "hashed-password",
            merchantStatus = MerchantStatus.ADMINS_ONLY
        )

        every { req.name } returns "Test Merchant"
        every { req.phoneNumber } returns "+123456789"
        every { req.merchantAddress } returns "Tokyo Station"
        every { req.merchantAddressCoordinate } returns merchantCoordinate
        every { req.password } returns "plain-password"

        every { merchantProfileRepository.existsByPhoneNumber(any()) } returns false
        every { passwordService.hashPassword(any()) } returns "hashed-password"

        val merchantSlot = slot<MerchantProfile>()
        every { merchantProfileRepository.save(capture(merchantSlot)) } returns savedMerchant
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = merchantAuthService.signupMerchant(req)

        assertSame(jwtTokens, result)

        val capturedMerchant = merchantSlot.captured
        assertEquals("Test Merchant", capturedMerchant.name)
        assertEquals("+123456789", capturedMerchant.phoneNumber)
        assertEquals("Tokyo Station", capturedMerchant.merchantAddress)
        assertEquals(merchantCoordinate, capturedMerchant.merchantAddressCoordinate)
        assertEquals("hashed-password", capturedMerchant.passwordHash)
        assertEquals(MerchantStatus.ADMINS_ONLY, capturedMerchant.merchantStatus)

        verify(exactly = 1) { merchantProfileRepository.existsByPhoneNumber(any()) }
        verify(exactly = 1) { passwordService.hashPassword(any()) }
        verify(exactly = 1) { merchantProfileRepository.save(any()) }
        verify(exactly = 1) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(merchantProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `loginMerchant should throw AccountNotFoundException when merchant does not exist`() {
        val req = mockk<MerchantLoginDTO>()

        every { req.phoneNumber } returns "+123456789"
        every { merchantProfileRepository.findByPhoneNumber(any()) } returns null

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            merchantAuthService.loginMerchant(req)
        }

        assertEquals(
            "Merchant with the phone number does not exist. Please sign up first.",
            thrown.message
        )

        verify(exactly = 1) { merchantProfileRepository.findByPhoneNumber(any()) }
        verify(exactly = 0) { passwordService.isPasswordValid(any(), any()) }
        verify(exactly = 0) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(merchantProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `loginMerchant should throw IncorrectPasswordException when password is invalid`() {
        val req = mockk<MerchantLoginDTO>()
        val savedMerchant = buildMerchantProfile(
            id = UUID.randomUUID(),
            name = "Test Merchant",
            phoneNumber = "+198765432",
            merchantAddress = "Shibuya",
            merchantAddressCoordinate = Coordinate(latitude = 35.6595, longitude = 139.7005),
            passwordHash = "stored-hash",
            merchantStatus = MerchantStatus.ADMINS_ONLY
        )

        every { req.phoneNumber } returns "+198765432"
        every { req.password } returns "wrong-password"
        every { merchantProfileRepository.findByPhoneNumber(any()) } returns savedMerchant
        every { passwordService.isPasswordValid(any(), any()) } returns false

        val thrown = assertThrows(IncorrectPasswordException::class.java) {
            merchantAuthService.loginMerchant(req)
        }

        assertEquals("Incorrect password", thrown.message)
    }

    @Test
    fun `loginMerchant should return jwt tokens when credentials are valid`() {
        val req = mockk<MerchantLoginDTO>()
        val jwtTokens = mockk<JwtTokensDTO>()
        val savedMerchant = buildMerchantProfile(
            id = UUID.randomUUID(),
            name = "Valid Merchant",
            phoneNumber = "+111222333",
            merchantAddress = "Osaka",
            merchantAddressCoordinate = Coordinate(latitude = 34.6937, longitude = 135.5023),
            passwordHash = "stored-hash",
            merchantStatus = MerchantStatus.ADMINS_ONLY
        )

        every { req.phoneNumber } returns "+111222333"
        every { req.password } returns "correct-password"
        every { merchantProfileRepository.findByPhoneNumber(any()) } returns savedMerchant
        every { passwordService.isPasswordValid(any(), any()) } returns true
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = merchantAuthService.loginMerchant(req)

        assertSame(jwtTokens, result)
    }

    @Test
    fun `refreshToken should throw InvalidJwtTokenException when merchant is not found`() {
        val req = mockk<TokenRefreshDTO>()
        val refreshTokenClaim = mockk<RefreshTokenClaim>()
        val merchantId = UUID.randomUUID()

        every { req.refreshToken } returns "refresh-token"
        every { refreshTokenClaim.accountId } returns merchantId
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = any()
            )
        } returns refreshTokenClaim
        every { merchantProfileRepository.findById(any()) } returns Optional.empty()

        val thrown = assertThrows(InvalidJwtTokenException::class.java) {
            merchantAuthService.refreshToken(req)
        }

        assertEquals("Merchant not found for the given token", thrown.message)

        verify(exactly = 1) {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = any()
            )
        }
        verify(exactly = 1) { merchantProfileRepository.findById(any()) }
        verify(exactly = 0) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(merchantProfileRepository, passwordService, jwtTokenService)
    }

    @Test
    fun `refreshToken should return new jwt tokens when token is valid`() {
        val req = mockk<TokenRefreshDTO>()
        val refreshTokenClaim = mockk<RefreshTokenClaim>()
        val jwtTokens = mockk<JwtTokensDTO>()
        val merchantId = UUID.randomUUID()

        val savedMerchant = buildMerchantProfile(
            id = merchantId,
            name = "Refresh Merchant",
            phoneNumber = "+444555666",
            merchantAddress = "Nagoya",
            merchantAddressCoordinate = Coordinate(latitude = 35.1815, longitude = 136.9066),
            passwordHash = "stored-hash",
            merchantStatus = MerchantStatus.ADMINS_ONLY
        )

        every { req.refreshToken } returns "valid-refresh-token"
        every { refreshTokenClaim.accountId } returns merchantId
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = any()
            )
        } returns refreshTokenClaim
        every { merchantProfileRepository.findById(any()) } returns Optional.of(savedMerchant)
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = merchantAuthService.refreshToken(req)

        assertSame(jwtTokens, result)

        verify(exactly = 1) {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = any()
            )
        }
        verify(exactly = 1) { merchantProfileRepository.findById(any()) }
        verify(exactly = 1) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
        confirmVerified(merchantProfileRepository, passwordService, jwtTokenService)
    }

    private fun buildMerchantProfile(
        id: UUID,
        name: String,
        phoneNumber: String,
        merchantAddress: String,
        merchantAddressCoordinate: Coordinate,
        passwordHash: String,
        merchantStatus: MerchantStatus
    ): MerchantProfile {
        return MerchantProfile(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            merchantAddress = merchantAddress,
            merchantAddressCoordinate = merchantAddressCoordinate,
            passwordHash = passwordHash,
            merchantStatus = merchantStatus
        )
    }
}