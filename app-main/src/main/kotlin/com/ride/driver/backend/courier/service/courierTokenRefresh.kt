package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.courier.repository.CourierRepository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.mapper.toAccessTokenClaim
import com.ride.driver.backend.courier.mapper.toRefreshTokenClaim

@Service
class CourierTokenRefreshService(
    private val jwtTokenService: JwtTokenService,
    private val courierRepository: CourierRepository
){
    fun refreshToken(
        req: TokenRefreshDTO,
    ): JwtTokensDTO{
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
        val accountDetails: AccessTokenClaim = jwtTokenService.extractRefreshTokenClaims(req.refreshToken)
        val courierProfile: CourierProfile = courierRepository.findById(accountDetails.accountId)
            ?: throw InvalidJwtTokenException("Courier not found for the given token")
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = courierProfile.toAccessTokenClaim(),
            refreshTokenClaim = courierProfile.toRefreshTokenClaim()
        )
    }
}