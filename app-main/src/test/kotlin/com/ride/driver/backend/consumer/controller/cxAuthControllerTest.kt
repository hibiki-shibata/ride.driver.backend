package com.ride.driver.backend.consumer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.service.ConsumerAuthService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify


@WebMvcTest(ConsumerAuthController::class)
class ConsumerAuthControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {

    @MockitoBean
    private lateinit var consumerAuthService: ConsumerAuthService

    @Test
    fun `signup returns 201 with location and tokens`() {
        val req = ConsumerSignupDTO(
            emailAddress = "test@example.com",
            password = "Password123!",
            name = "Test User",
            consumerAddress = "123 Main St",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917)
        )

        val tokens = JwtTokensDTO(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )

        given(consumerAuthService.signupConsumer(any())).willReturn(tokens)

        mockMvc.perform(
            post("/api/v1/consumers/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "/api/v1/consumers/me"))
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))

        verify(consumerAuthService).signupConsumer(any())
    }

    @Test
    fun `login returns 200 with tokens`() {
        val req = ConsumerLoginDTO(
            emailAddress = "test@example.com",
            password = "Password123!"
        )

        val tokens = JwtTokensDTO(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )

        given(consumerAuthService.loginConsumer(any())).willReturn(tokens)

        mockMvc.perform(
            post("/api/v1/consumers/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))

        verify(consumerAuthService).loginConsumer(any())
    }

    @Test
    fun `refresh token returns 200 with new tokens`() {
        val req = TokenRefreshDTO(refreshToken = "old-refresh-token")

        val tokens = JwtTokensDTO(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token"
        )

        given(consumerAuthService.refreshToken(any())).willReturn(tokens)

        mockMvc.perform(
            post("/api/v1/consumers/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("new-access-token"))
            .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))

        verify(consumerAuthService).refreshToken(any())
    }
}