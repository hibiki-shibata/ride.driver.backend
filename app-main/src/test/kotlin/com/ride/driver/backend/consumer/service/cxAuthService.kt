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

class ConsumerAuthControllerUnitTestt(
) {
    private val consumerAuthService = mockk<ConsumerAuthService>()
    private val ConsumerAuthController = ConsumerAuthController(consumerAuthService)

    val signupReq = ConsumerSignupDTO(
        name = "Hibiki",
        emailAddress = "a@gmail.com",
        consumerAddress = "Tokyo",
        consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
        password = "password123"
    )

    val loginReq = mockk<ConsumerLoginDTO>()
    val refreshReq = mockk<TokenRefreshDTO>()

    val res = JwtTokensDTO(
        accessToken = "fake_access_token",
        refreshToken = "fake_refresh_token"
    )

    @Test
    fun `consumerSignup returns 201 with body`() {
        every { consumerAuthService.signupConsumer(signupReq) } returns res
        val result = ConsumerAuthController.consumerSignup(signupReq)
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(res, result.body)
    }

    @Test
    fun `consumerSignup throws AccountConflictException when email already exists`() {
        every { consumerAuthService.signupConsumer(signupReq) } throws AccountNotFoundException("Consumer with request email address already exists")
        Assertions.assertThrows(AccountNotFoundException::class.java) {
            ConsumerAuthController.consumerSignup(signupReq)
        }
    }

    @Test
    fun `consumerSignup throws generic exception when unexpected error occurs`() {
        every { consumerAuthService.signupConsumer(signupReq) } throws RuntimeException("Unexpected error")
        Assertions.assertThrows(RuntimeException::class.java) {
            ConsumerAuthController.consumerSignup(signupReq)
        }
    }

    @Test
    fun `consumerLogin returns 200 with body`() {
        every { consumerAuthService.loginConsumer(loginReq) } returns res
        val result = ConsumerAuthController.consumerLogin(loginReq)
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(res, result.body)
    }

    @Test
    fun `consumerLogin throws AccountNotFoundException when account does not exist`() {
        every { consumerAuthService.loginConsumer(loginReq) } throws AccountNotFoundException("Consumer account not found")
        Assertions.assertThrows(AccountNotFoundException::class.java) {
            ConsumerAuthController.consumerLogin(loginReq)
        }
    }

    @Test
    fun `consumerLogin throws generic exception when unexpected error occurs`() {
        every { consumerAuthService.loginConsumer(loginReq) } throws RuntimeException("Unexpected error")
        Assertions.assertThrows(RuntimeException::class.java) {
            ConsumerAuthController.consumerLogin(loginReq)
        }
    }

    @Test
    fun `refreshToken returns 200 with body`() {
        every { consumerAuthService.refreshToken(refreshReq) } returns res
        val result = ConsumerAuthController.refreshToken(refreshReq)
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertEquals(res, result.body)
    }

    @Test
    fun `refreshToken throws AccountNotFoundException when refresh token is invalid`() {
        every { consumerAuthService.refreshToken(refreshReq) } throws AccountNotFoundException("Invalid refresh token")
        Assertions.assertThrows(AccountNotFoundException::class.java) {
            ConsumerAuthController.refreshToken(refreshReq)
        }
    }

    @Test
    fun `refreshToken throws generic exception when unexpected error occurs`() {
        every { consumerAuthService.refreshToken(refreshReq) } throws RuntimeException("Unexpected error")
        Assertions.assertThrows(RuntimeException::class.java) {
            ConsumerAuthController.refreshToken(refreshReq)
        }
    }
}