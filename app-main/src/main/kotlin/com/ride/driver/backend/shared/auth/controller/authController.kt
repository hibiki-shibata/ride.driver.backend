package com.ride.driver.backend.shared.auth.controller

import java.util.UUID
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exceptions.BadRequestException

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val jwtTokenService: JwtTokenService,
) {
    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody @Valid req: JwtTokensDTO): ResponseEntity<JwtTokensDTO> {
        val accountIdOfRefreshToken: UUID = jwtTokenService.extractAccountId(req.refreshToken)
        val acccountIdOfAccessToken: UUID = jwtTokenService.extractAccountId(req.accessToken)
        if (accountIdOfRefreshToken != acccountIdOfAccessToken) throw BadRequestException("Account ID in refresh token does not match account ID in access token")
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw BadRequestException("Invalid or expired refresh token")
        val accountName: String = jwtTokenService.extractAccountName(req.accessToken)
        val newAccessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenData(
                accountID = accountIdOfRefreshToken,
                accountName = accountName,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(accountIdOfRefreshToken)
        return ResponseEntity.ok(JwtTokensDTO(newAccessToken, newRefreshToken))
    }
}