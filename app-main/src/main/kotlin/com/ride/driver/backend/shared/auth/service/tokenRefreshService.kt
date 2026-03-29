// package com.ride.driver.backend.shared.auth.service

// import org.springframework.stereotype.Service
// import java.util.UUID
// import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
// import com.ride.driver.backend.shared.auth.domain.AccountRoles
// import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
// import com.ride.driver.backend.shared.auth.dto.TokenRefreshDTO
// import com.ride.driver.backend.shared.auth.service.JwtTokenService
// import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

// @Service
// class TokenRefreshService(
//     private val jwtTokenService: JwtTokenService,
//     private val consumer
// ){
//     fun refreshToken(
//         req: TokenRefreshDTO,
//     ): JwtTokensDTO{
//         if (!jwtTokenService.isTokenValid(req.refreshToken)) throw InvalidJwtTokenException("Refresh token is either expired or invalid")
//         val accountDetails: AccessTokenClaim = jwtTokenService.extractRefreshTokenClaim(req.refreshToken)
//         when(accountDetails.accountRoles.firstOrNull()) {
//             AccountRoles.BASE_CONSUMER_ROLE -> {
//                 return generateNewTokens(accountDetails)
//             }
//             AccountRoles.BASE_COURIER_ROLE -> {
//                 return generateNewTokens(accountDetails)
//             }
//             AccountRoles.BASE_MERCHANT_ROLE -> {
//                 return generateNewTokens(accountDetails)            
//             }
//             else -> throw InvalidJwtTokenException("Invalid account role in token")
//         }
//         return jwtTokenService.generateAccessTokenAndRefreshToken(
//             AccessTokenClaim(
//                 accountId = accountDetails.accountId,
//                 accountName = accountDetails.accountName,
//                 accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
//             ),
//             RefreshTokenClaim(
//                 accountId = accountDetails.accountId
//             )
//         )
//         return JwtTokensDTO( newAccessToken, newRefreshToken)
//     }
// }