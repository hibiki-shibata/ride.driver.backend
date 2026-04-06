package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class ConsumerAuthControllerTest {

    private lateinit var consumerAuthService: ConsumerAuthService
    private lateinit var consumerAuthController: ConsumerAuthController

    @BeforeEach
    fun setUp() {
        consumerAuthService = mockk()
        consumerAuthController = ConsumerAuthController(consumerAuthService)
    }

    @Test
    fun `consumerSignup should return 200 ok with access token and refresh token cookie`() {
        val request = ConsumerSignupDTO(
            name = "Hibiki",
            emailAddress = "a@gmail.com",
            consumerAddress = "Tokyo",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
            password = "password123"
        )
        val jwtTokens = JwtTokens(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        every { consumerAuthService.signupConsumer(request) } returns jwtTokens

        val response = consumerAuthController.consumerSignup(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("fake_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "fake_refresh_token",
            expectedPath = "/api/v1/consumer/auth/refresh-token"
        )

        verify(exactly = 1) { consumerAuthService.signupConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `consumerSignup should propagate AccountNotFoundException when email already exists`() {
        val request = ConsumerSignupDTO(
            name = "Hibiki",
            emailAddress = "a@gmail.com",
            consumerAddress = "Tokyo",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
            password = "password123"
        )

        every { consumerAuthService.signupConsumer(request) } throws
            AccountNotFoundException("Consumer with request email address already exists")

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.consumerSignup(request)
        }

        assertEquals("Consumer with request email address already exists", thrown.message)

        verify(exactly = 1) { consumerAuthService.signupConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `consumerSignup should propagate RuntimeException when unexpected error occurs`() {
        val request = ConsumerSignupDTO(
            name = "Hibiki",
            emailAddress = "a@gmail.com",
            consumerAddress = "Tokyo",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
            password = "password123"
        )

        every { consumerAuthService.signupConsumer(request) } throws
            RuntimeException("Unexpected error")

        val thrown = assertThrows(RuntimeException::class.java) {
            consumerAuthController.consumerSignup(request)
        }

        assertEquals("Unexpected error", thrown.message)

        verify(exactly = 1) { consumerAuthService.signupConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `consumerLogin should return 200 ok with access token and refresh token cookie`() {
        val request = mockk<ConsumerLoginDTO>()
        val jwtTokens = JwtTokens(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        every { consumerAuthService.loginConsumer(request) } returns jwtTokens

        val response = consumerAuthController.consumerLogin(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("fake_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "fake_refresh_token",
            expectedPath = "/api/v1/consumer/auth/refresh-token"
        )

        verify(exactly = 1) { consumerAuthService.loginConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `consumerLogin should propagate AccountNotFoundException when account does not exist`() {
        val request = mockk<ConsumerLoginDTO>()

        every { consumerAuthService.loginConsumer(request) } throws
            AccountNotFoundException("Consumer account not found")

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.consumerLogin(request)
        }

        assertEquals("Consumer account not found", thrown.message)

        verify(exactly = 1) { consumerAuthService.loginConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `consumerLogin should propagate RuntimeException when unexpected error occurs`() {
        val request = mockk<ConsumerLoginDTO>()

        every { consumerAuthService.loginConsumer(request) } throws
            RuntimeException("Unexpected error")

        val thrown = assertThrows(RuntimeException::class.java) {
            consumerAuthController.consumerLogin(request)
        }

        assertEquals("Unexpected error", thrown.message)

        verify(exactly = 1) { consumerAuthService.loginConsumer(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `refreshToken should return 200 ok with new access token and refresh token cookie`() {
        val request = "valid_refresh_token"
        val jwtTokens = JwtTokens(
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token"
        )

        every { consumerAuthService.refreshToken(request) } returns jwtTokens

        val response = consumerAuthController.refreshToken(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("new_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "new_refresh_token",
            expectedPath = "/api/v1/consumer/auth/refresh-token"
        )

        verify(exactly = 1) { consumerAuthService.refreshToken(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `refreshToken should propagate AccountNotFoundException when refresh token is invalid`() {
        val request = "invalid_refresh_token"

        every { consumerAuthService.refreshToken(request) } throws
            AccountNotFoundException("Invalid refresh token")

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            consumerAuthController.refreshToken(request)
        }

        assertEquals("Invalid refresh token", thrown.message)

        verify(exactly = 1) { consumerAuthService.refreshToken(request) }
        confirmVerified(consumerAuthService)
    }

    @Test
    fun `refreshToken should propagate RuntimeException when unexpected error occurs`() {
        val request = "valid_refresh_token"

        every { consumerAuthService.refreshToken(request) } throws
            RuntimeException("Unexpected error")

        val thrown = assertThrows(RuntimeException::class.java) {
            consumerAuthController.refreshToken(request)
        }

        assertEquals("Unexpected error", thrown.message)

        verify(exactly = 1) { consumerAuthService.refreshToken(request) }
        confirmVerified(consumerAuthService)
    }

    private fun assertRefreshTokenCookie(
        setCookieHeader: String?,
        expectedRefreshToken: String,
        expectedPath: String
    ) {
        assertNotNull(setCookieHeader)
        assertTrue(setCookieHeader!!.contains("refreshToken=$expectedRefreshToken"))
        assertTrue(setCookieHeader.contains("HttpOnly"))
        assertTrue(setCookieHeader.contains("Secure"))
        assertTrue(setCookieHeader.contains("Path=$expectedPath"))
        assertTrue(setCookieHeader.contains("Max-Age=604800"))
    }
}