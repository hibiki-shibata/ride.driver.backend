package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID

class ConsumerAuthControllerUnitTest(
) {
    private val consumerAuthService = mockk<ConsumerAuthService>()
    private val ConsumerAuthController = ConsumerAuthController(consumerAuthService)

    val req = ConsumerSignupDTO(
        name = "Hibiki",
        emailAddress = "a@gmail.com",
        consumerAddress = "Tokyo",
        consumerAddressCoordinate = Coordinate(35.6895, 139.6917),
        password = "password123"
    )

    val res = JwtTokensDTO(
        accessToken = "fake_access_token",
        refreshToken = "fake_refresh_token"
    )

    @Test
    fun `consumerSignup returns 201 with body`() {
        every { consumerAuthService.signupConsumer(req) } returns res
        val result = ConsumerAuthController.consumerSignup(req)
        Assertions.assertEquals(HttpStatus.CREATED, result.statusCode)
        Assertions.assertEquals(res, result.body)
    }

    @Test
    fun `consumerSignup throws AccountConflictException when email already exists`() {
        every { consumerAuthService.signupConsumer(req) } throws AccountNotFoundException("Consumer with request email address already exists") 
        Assertions.assertThrows(AccountNotFoundException::class.java) {
            ConsumerAuthController.consumerSignup(req)
        }
    }

    @Test
    fun `consumerSignup throws generic exception when unexpected error occurs`() {
        every { consumerAuthService.signupConsumer(req) } throws RuntimeException("Unexpected error")
        Assertions.assertThrows(RuntimeException::class.java) {
            ConsumerAuthController.consumerSignup(req)  
        }
    }
}