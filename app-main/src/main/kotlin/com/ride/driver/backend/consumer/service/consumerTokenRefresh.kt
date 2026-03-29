package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.mapper.toAccessTokenClaim
import com.ride.driver.backend.consumer.mapper.toRefreshTokenClaim

@Service
class ConsumerTokenRefreshService(
    private val jwtTokenService: JwtTokenService,
    private val consumerProfileRepository: ConsumerProfileRepository
){
    fun refreshToken(
        req: TokenRefreshDTO,
    ): JwtTokensDTO{
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
        val accountDetails: RefreshTokenClaim = jwtTokenService.extractRefreshTokenClaim(req.refreshToken)
        val consumerProfile: ConsumerProfile = consumerProfileRepository.findById(accountDetails.accountId).orElseThrow {
             InvalidJwtTokenException("Consumer not found for the given token")
        }
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = consumerProfile.toAccessTokenClaim(),
            refreshTokenClaim = consumerProfile.toRefreshTokenClaim()
        )
    }
}