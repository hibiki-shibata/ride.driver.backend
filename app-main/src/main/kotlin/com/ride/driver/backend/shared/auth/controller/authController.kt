package com.ride.driver.backend.shared.auth.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.shared.auth.service.TokenRefreshService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val tokenRefreshService: TokenRefreshService,
) {
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: TokenRefreshDTO): ResponseEntity<JwtTokensDTO> {
        val refreshedJwtTokens: JwtTokensDTO = tokenRefreshService.refreshToken(req)
        return ResponseEntity.ok(refreshedJwtTokens)
    }
}