package com.ride.driver.backend.merchant.controller

import com.ride.driver.backend.merchant.dto.MerchantLoginDTO
import com.ride.driver.backend.merchant.dto.MerchantSignupDTO
import com.ride.driver.backend.merchant.service.MerchantAuthService
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
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

class MerchantAuthControllerTest {

    private lateinit var merchantAuthService: MerchantAuthService
    private lateinit var merchantAuthController: MerchantAuthController

    @BeforeEach
    fun setUp() {
        merchantAuthService = mockk()
        merchantAuthController = MerchantAuthController(merchantAuthService)
    }

    @Test
    fun `merchantSignup should return 200 ok with access token and refresh token cookie`() {
        val request = mockk<MerchantSignupDTO>()
        val jwtTokens = JwtTokens(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        every { merchantAuthService.signupMerchant(request) } returns jwtTokens

        val response = merchantAuthController.merchantSignup(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("fake_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "fake_refresh_token",
            expectedPath = "/api/v1/merchants/auth/refresh-token"
        )

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
    fun `merchantLogin should return 200 ok with access token and refresh token cookie`() {
        val request = mockk<MerchantLoginDTO>()
        val jwtTokens = JwtTokens(
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        )

        every { merchantAuthService.loginMerchant(request) } returns jwtTokens

        val response = merchantAuthController.merchantLogin(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("fake_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "fake_refresh_token",
            expectedPath = "/api/v1/merchants/auth/refresh-token"
        )

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
    fun `refreshToken should return 200 ok with new access token and refresh token cookie`() {
        val request = mockk<TokenRefreshDTO>()
        val jwtTokens = JwtTokens(
            accessToken = "new_access_token",
            refreshToken = "new_refresh_token"
        )

        every { merchantAuthService.refreshToken(request) } returns jwtTokens

        val response = merchantAuthController.refreshToken(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("new_access_token", response.body?.accessToken)

        assertRefreshTokenCookie(
            setCookieHeader = response.headers.getFirst(HttpHeaders.SET_COOKIE),
            expectedRefreshToken = "new_refresh_token",
            expectedPath = "/api/v1/merchants/auth/refresh-token"
        )

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