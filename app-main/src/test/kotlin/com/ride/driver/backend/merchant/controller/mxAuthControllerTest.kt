package com.ride.driver.backend.merchant.controller

import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
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

class MerchantAuthControllerTest {

    private lateinit var merchantAuthService: MerchantAuthService
    private lateinit var merchantAuthController: MerchantAuthController

    @BeforeEach
    fun setUp() {
        merchantAuthService = mockk()
        merchantAuthController = MerchantAuthController(merchantAuthService)
    }

    @Test
    fun `merchantSignup should return 201 created with jwt tokens and location header`() {
        val request = mockk<MerchantSignupDTO>()
        val jwtTokens = mockk<JwtTokensDTO>()

        every { merchantAuthService.signupMerchant(request) } returns jwtTokens

        val response = merchantAuthController.merchantSignup(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(URI("/api/v1/merchants/me"), response.headers.location)
        assertSame(jwtTokens, response.body)

        verify(exactly = 1) { merchantAuthService.signupMerchant(request) }
        confirmVerified(merchantAuthService)
    }

    @Test
    fun `merchantSignup should propagate exception when service throws`() {
        val request = mockk<MerchantSignupDTO>()
        val exception = RuntimeException("signup failed")

        every { merchantAuthService.signupMerchant(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantAuthController.merchantSignup(request)
        }

        assertEquals("signup failed", thrown.message)

        verify(exactly = 1) { merchantAuthService.signupMerchant(request) }
        confirmVerified(merchantAuthService)
    }

    @Test
    fun `merchantLogin should return 200 ok with jwt tokens`() {
        val request = mockk<MerchantLoginDTO>()
        val jwtTokens = mockk<JwtTokensDTO>()

        every { merchantAuthService.loginMerchant(request) } returns jwtTokens

        val response = merchantAuthController.merchantLogin(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(jwtTokens, response.body)

        verify(exactly = 1) { merchantAuthService.loginMerchant(request) }
        confirmVerified(merchantAuthService)
    }

    @Test
    fun `merchantLogin should propagate exception when service throws`() {
        val request = mockk<MerchantLoginDTO>()
        val exception = RuntimeException("invalid credentials")

        every { merchantAuthService.loginMerchant(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantAuthController.merchantLogin(request)
        }

        assertEquals("invalid credentials", thrown.message)

        verify(exactly = 1) { merchantAuthService.loginMerchant(request) }
        confirmVerified(merchantAuthService)
    }

    @Test
    fun `refreshToken should return 200 ok with new jwt tokens`() {
        val request = mockk<TokenRefreshDTO>()
        val newJwtTokens = mockk<JwtTokensDTO>()

        every { merchantAuthService.refreshToken(request) } returns newJwtTokens

        val response = merchantAuthController.refreshToken(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(newJwtTokens, response.body)

        verify(exactly = 1) { merchantAuthService.refreshToken(request) }
        confirmVerified(merchantAuthService)
    }

    @Test
    fun `refreshToken should propagate exception when service throws`() {
        val request = mockk<TokenRefreshDTO>()
        val exception = RuntimeException("refresh token invalid")

        every { merchantAuthService.refreshToken(request) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            merchantAuthController.refreshToken(request)
        }

        assertEquals("refresh token invalid", thrown.message)

        verify(exactly = 1) { merchantAuthService.refreshToken(request) }
        confirmVerified(merchantAuthService)
    }
}