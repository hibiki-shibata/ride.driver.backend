package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ConsumerAuthControllerUnitTest {

    private val consumerAuthService = mockk<ConsumerAuthService>()
    private val consumerAuthController = ConsumerAuthController(consumerAuthService)

    private val signupReq = ConsumerSignupDTO(
        name = "Hibiki",
        emailAddress = "a@gmail.com",
        consumerAddress = "Tokyo",
        consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
        password = "password123"
    )

    // Keep mocked if you want to stay aligned with your current simple unit-test style.
    private val loginReq = mockk<ConsumerLoginDTO>()
    private val refreshReq = mockk<TokenRefreshDTO>()

    private val jwtTokens = JwtTokensDTO(
        accessToken = "fake_access_token",
        refreshToken = "fake_refresh_token"
    )

    @Test
    fun `consumerSignup returns 201 with body and location header`() {
        every { consumerAuthService.signupConsumer(signupReq) } returns jwtTokens

        val result = consumerAuthController.consumerSignup(signupReq)

        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(jwtTokens, result.body)
        Assertions.assertEquals("/api/v1/consumers/me", result.headers.location?.toString())
    }

    @Test
    fun `consumerSignup throws AccountNotFoundException when email already exists`() {
        every { consumerAuthService.signupConsumer(signupReq) } throws
            AccountNotFoundException("Consumer with request email address already exists")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.consumerSignup(signupReq)
        }
    }

    @Test
    fun `consumerSignup throws RuntimeException when unexpected error occurs`() {
        every { consumerAuthService.signupConsumer(signupReq) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerAuthController.consumerSignup(signupReq)
        }
    }

    @Test
    fun `consumerLogin returns 200 with body`() {
        every { consumerAuthService.loginConsumer(loginReq) } returns jwtTokens

        val result = consumerAuthController.consumerLogin(loginReq)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(jwtTokens, result.body)
    }

    @Test
    fun `consumerLogin throws AccountNotFoundException when account does not exist`() {
        every { consumerAuthService.loginConsumer(loginReq) } throws
            AccountNotFoundException("Consumer account not found")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.consumerLogin(loginReq)
        }
    }

    @Test
    fun `consumerLogin throws RuntimeException when unexpected error occurs`() {
        every { consumerAuthService.loginConsumer(loginReq) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerAuthController.consumerLogin(loginReq)
        }
    }

    @Test
    fun `refreshToken returns 200 with body`() {
        every { consumerAuthService.refreshToken(refreshReq) } returns jwtTokens

        val result = consumerAuthController.refreshToken(refreshReq)

        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(jwtTokens, result.body)
    }

    @Test
    fun `refreshToken throws AccountNotFoundException when refresh token is invalid`() {
        every { consumerAuthService.refreshToken(refreshReq) } throws
            AccountNotFoundException("Invalid refresh token")

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.refreshToken(refreshReq)
        }
    }

    @Test
    fun `refreshToken throws RuntimeException when unexpected error occurs`() {
        every { consumerAuthService.refreshToken(refreshReq) } throws
            RuntimeException("Unexpected error")

        Assertions.assertThrows(RuntimeException::class.java) {
            consumerAuthController.refreshToken(refreshReq)
        }
    }
}