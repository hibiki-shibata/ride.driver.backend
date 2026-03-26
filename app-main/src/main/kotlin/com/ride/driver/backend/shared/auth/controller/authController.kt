package com.ride.driver.backend.shared.auth.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.shared.auth.service.RefreshTokenService
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val refreshTokenService: RefreshTokenService,
) {
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: JwtTokensDTO): ResponseEntity<JwtTokensDTO> {
        val refreshedJwtTokens: JwtTokensDTO = refreshTokenService.updateRefreshToken(req)
        return ResponseEntity.ok(refreshedJwtTokens)
    }
}