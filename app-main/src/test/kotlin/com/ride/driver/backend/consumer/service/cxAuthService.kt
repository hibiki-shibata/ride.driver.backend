package com.ride.driver.backend.consumer.service

import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.UUID

class ConsumerAuthServiceUnitTest {

    private val consumerProfileRepository = mockk<ConsumerProfileRepository>()
    private val passwordService = mockk<PasswordService>()
    private val jwtTokenService = mockk<JwtTokenService>()

    private val consumerAuthService = ConsumerAuthService(
        consumerProfileRepository = consumerProfileRepository,
        passwordService = passwordService,
        jwtTokenService = jwtTokenService
    )

    private val signupReq = ConsumerSignupDTO(
        name = "Hibiki",
        emailAddress = "a@gmail.com",
        consumerAddress = "Tokyo",
        consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
        password = "password123"
    )

    private val loginReq = mockk<ConsumerLoginDTO>()
    private val refreshReq = "valid_refresh_token"
    private val savedConsumer = mockk<ConsumerProfile>(relaxed = true)
    private val refreshTokenClaim = mockk<RefreshTokenClaim>()

    private val jwtTokens = JwtTokens(
        accessToken = "fake_access_token",
        refreshToken = "fake_refresh_token"
    )

    private val hashedPassword = "hashed_password"
    private val accountId = UUID.randomUUID()

    init {
        every { loginReq.emailAddress } returns "a@gmail.com"
        every { loginReq.password } returns "password123"

        every { savedConsumer.id } returns accountId
        every { savedConsumer.passwordHash } returns hashedPassword
        every { savedConsumer.emailAddress } returns "a@gmail.com"

        every { refreshTokenClaim.accountId } returns accountId
    }

    @Test
    fun `signupConsumer returns JWT tokens when signup succeeds`() {
        every { consumerProfileRepository.existsByEmailAddress(signupReq.emailAddress) } returns false
        every { passwordService.hashPassword(signupReq.password) } returns hashedPassword
        every { consumerProfileRepository.save(any()) } returns savedConsumer
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = consumerAuthService.signupConsumer(signupReq)

        Assertions.assertEquals(jwtTokens, result)

        verify(exactly = 1) { consumerProfileRepository.existsByEmailAddress(signupReq.emailAddress) }
        verify(exactly = 1) { passwordService.hashPassword(signupReq.password) }
        verify(exactly = 1) {
            consumerProfileRepository.save(
                match {
                    it.name == signupReq.name &&
                    it.emailAddress == signupReq.emailAddress &&
                    it.consumerAddress == signupReq.consumerAddress &&
                    it.consumerAddressCoordinate == signupReq.consumerAddressCoordinate &&
                    it.passwordHash == hashedPassword
                }
            )
        }
        verify(exactly = 1) { jwtTokenService.generateAccessTokenAndRefreshToken(any()) }
    }

    @Test
    fun `signupConsumer throws AccountConflictException when email already exists`() {
        every { consumerProfileRepository.existsByEmailAddress(signupReq.emailAddress) } returns true

        Assertions.assertThrows(AccountConflictException::class.java) {
            consumerAuthService.signupConsumer(signupReq)
        }
    }

    @Test
    fun `loginConsumer returns JWT tokens when login succeeds`() {
        every { consumerProfileRepository.findByEmailAddress(any()) } returns savedConsumer
        every {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = hashedPassword
            )
        } returns true
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = consumerAuthService.loginConsumer(loginReq)

        Assertions.assertEquals(jwtTokens, result)
    }

    @Test
    fun `loginConsumer throws AccountNotFoundException when consumer does not exist`() {
        every { consumerProfileRepository.findByEmailAddress(any()) } returns null

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerAuthService.loginConsumer(loginReq)
        }
    }

    @Test
    fun `loginConsumer throws IncorrectPasswordException when password is invalid`() {
        every { consumerProfileRepository.findByEmailAddress(any()) } returns savedConsumer
        every {
            passwordService.isPasswordValid(
                inputPassword = any(),
                storedHashedPassword = hashedPassword
            )
        } returns false

        Assertions.assertThrows(IncorrectPasswordException::class.java) {
            consumerAuthService.loginConsumer(loginReq)
        }
    }

    @Test
    fun `refreshToken returns JWT tokens when refresh token is valid`() {
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = ServiceType.CONSUMER
            )
        } returns refreshTokenClaim
        every { consumerProfileRepository.findById(accountId) } returns Optional.of(savedConsumer)
        every { jwtTokenService.generateAccessTokenAndRefreshToken(any()) } returns jwtTokens

        val result = consumerAuthService.refreshToken(refreshReq)

        Assertions.assertEquals(jwtTokens, result)
    }

    @Test
    fun `refreshToken throws InvalidJwtTokenException when consumer does not exist for token`() {
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = ServiceType.CONSUMER
            )
        } returns refreshTokenClaim
        every { consumerProfileRepository.findById(accountId) } returns Optional.empty()

        Assertions.assertThrows(InvalidJwtTokenException::class.java) {
            consumerAuthService.refreshToken(refreshReq)
        }
    }

    @Test
    fun `refreshToken throws InvalidJwtTokenException when refresh token is invalid`() {
        every {
            jwtTokenService.extractRefreshTokenClaimAndValidate(
                token = any(),
                expectedServiceType = ServiceType.CONSUMER
            )
        } throws InvalidJwtTokenException("Invalid refresh token")

        Assertions.assertThrows(InvalidJwtTokenException::class.java) {
            consumerAuthService.refreshToken(refreshReq)
        }
    }
}