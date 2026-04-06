package com.ride.driver.backend.courier.controller

import com.ride.driver.backend.courier.dto.CourierLoginDTO
import com.ride.driver.backend.courier.dto.CourierSignupDTO
import com.ride.driver.backend.courier.service.CourierAuthService
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CourierAuthControllerTest {

    private lateinit var courierAuthService: CourierAuthService
    private lateinit var courierAuthController: CourierAuthController

    @BeforeEach
    fun setUp() {
        courierAuthService = mockk()
        courierAuthController = CourierAuthController(courierAuthService)
    }

    @Test
    fun `courierSignup should return 201 created with jwt tokens and location header`() {
        val request = mockk<CourierSignupDTO>()
        val jwtTokens = mockk<JwtTokens>()

        every { courierAuthService.signupCourier(request) } returns jwtTokens

        val response = courierAuthController.courierSignup(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(URI("/api/v1/couriers/me"), response.headers.location)
        assertSame(jwtTokens, response.body)

        verify(exactly = 1) { courierAuthService.signupCourier(request) }
        confirmVerified(courierAuthService)
    }

    @Test
    fun `courierSignup should propagate exception when service throws`() {
        val request = mockk<CourierSignupDTO>()
        val exception = RuntimeException("signup failed")

        every { courierAuthService.signupCourier(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierAuthController.courierSignup(request)
        }

        assertEquals("signup failed", thrown.message)

        verify(exactly = 1) { courierAuthService.signupCourier(request) }
        confirmVerified(courierAuthService)
    }

    @Test
    fun `courierLogin should return 200 ok with jwt tokens`() {
        val request = mockk<CourierLoginDTO>()
        val jwtTokens = mockk<JwtTokens>()

        every { courierAuthService.loginCourier(request) } returns jwtTokens

        val response = courierAuthController.courierLogin(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(jwtTokens, response.body)

        verify(exactly = 1) { courierAuthService.loginCourier(request) }
        confirmVerified(courierAuthService)
    }

    @Test
    fun `courierLogin should propagate exception when service throws`() {
        val request = mockk<CourierLoginDTO>()
        val exception = RuntimeException("invalid credentials")

        every { courierAuthService.loginCourier(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierAuthController.courierLogin(request)
        }

        assertEquals("invalid credentials", thrown.message)

        verify(exactly = 1) { courierAuthService.loginCourier(request) }
        confirmVerified(courierAuthService)
    }

    @Test
    fun `refreshToken should return 200 ok with new jwt tokens`() {
        val request = mockk<TokenRefreshDTO>()
        val newJwtTokens = mockk<JwtTokens>()

        every { courierAuthService.refreshToken(request) } returns newJwtTokens

        val response = courierAuthController.refreshToken(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(newJwtTokens, response.body)

        verify(exactly = 1) { courierAuthService.refreshToken(request) }
        confirmVerified(courierAuthService)
    }

    @Test
    fun `refreshToken should propagate exception when service throws`() {
        val request = mockk<TokenRefreshDTO>()
        val exception = RuntimeException("refresh token invalid")

        every { courierAuthService.refreshToken(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierAuthController.refreshToken(request)
        }

        assertEquals("refresh token invalid", thrown.message)

        verify(exactly = 1) { courierAuthService.refreshToken(request) }
        confirmVerified(courierAuthService)
    }
}