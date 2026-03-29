package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

@Service
class TokenRefreshService(
    private val jwtTokenService: JwtTokenService,
){
    fun refreshToken(req: JwtTokensDTO): JwtTokensDTO{
        val accountIdOfRefreshToken: UUID = jwtTokenService.extractAccountId(req.refreshToken)
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
        val accountName: String = jwtTokenService.extractAccountName(req.accessToken)
        val newAccessToken: String = jwtTokenService.generateAccessToken(
            AccessTokenClaim(
                accountId = accountIdOfRefreshToken,
                accountName = accountName,
                accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
            )
        )
        val newRefreshToken: String = jwtTokenService.generateRefreshToken(accountIdOfRefreshToken)
        return JwtTokensDTO( newAccessToken, newRefreshToken)
    }
}