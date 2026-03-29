package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.mapper.toAccessTokenClaim
import com.ride.driver.backend.merchant.mapper.toRefreshTokenClaim

@Service
class merchantTokenRefreshService(
    private val jwtTokenService: JwtTokenService,
    private val merchantProfileRepository: MerchantProfileRepository
){
    fun refreshToken(
        req: TokenRefreshDTO,
    ): JwtTokensDTO{
        if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
        val accountDetails: RefreshTokenClaim = jwtTokenService.extractRefreshTokenClaim(req.refreshToken)
        val merchantProfile: MerchantProfile = merchantProfileRepository.findById(accountDetails.accountId).orElseThrow {
             InvalidJwtTokenException("Merchant not found for the given token")
        }
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            accessTokenClaim = merchantProfile.toAccessTokenClaim(),
            refreshTokenClaim = merchantProfile.toRefreshTokenClaim()
        )
    }
}