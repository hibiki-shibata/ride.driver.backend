package com.ride.driver.backend.shared.auth.service

import java.util.Date
import java.util.UUID
import java.nio.charset.StandardCharsets
import java.security.Key
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.RefreshTokenClaim
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

@Service
open class JwtTokenService(
    private val accessTokenValidityInMilliseconds: Long = 36000000, // 10 hour
    private val refreshTokenValidityInMilliseconds: Long = 86400000, // 24 hours
    private val signingKeyString: String = "this-is-a-very-long-test-secret-key-that-is-at-least-32-bytes!",
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
) {    
    fun generateAccessTokenAndRefreshToken(
        accessTokenClaim: AccessTokenClaim,
        refreshTokenClaim: RefreshTokenClaim
    ): JwtTokensDTO {
        val accessToken = generateAccessToken(accessTokenClaim)
        val refreshToken = generateRefreshToken(refreshTokenClaim)
        return JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun generateAccessToken(
        accessTokenClaim: AccessTokenClaim
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims =  mapOf(
            "accountId" to accessTokenClaim.accountId.toString(),
            "accountRoles" to accessTokenClaim.accountRoles.map { it.name }
        )
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(accessTokenClaim.accountName)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    private fun generateRefreshToken(
        refreshTokenClaim: RefreshTokenClaim
    ): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setClaims(mapOf("accountId" to refreshTokenClaim.accountId.toString()))
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + refreshTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    fun extractAccessTokenClaim(token: String): AccessTokenClaim {
        val claims = extractAllClaims(token)
        return AccessTokenClaim(
            accountId = UUID.fromString(claims["accountId"].toString() ?: throw InvalidJwtTokenException("Account ID not found in token")),
            accountName = claims.subject ?: throw InvalidJwtTokenException("Account name not found in token"),
            accountRoles = (claims["accountRoles"] as List<AccountRoles>).map { AccountRoles.valueOf(it.toString()) }
        )
    }

    fun extractRefreshTokenClaim(token: String): RefreshTokenClaim {
        val claims = extractAllClaims(token)
        return RefreshTokenClaim(
            accountId = UUID.fromString(claims["accountId"].toString() ?: throw InvalidJwtTokenException("Account ID not found in token")),
            serviceType = claims["serviceType"] as ServiceType ?: throw InvalidJwtTokenException("Service type is not found in token")
            // type enum service
        )
    }    

    fun extractAccountName(token: String): String {
        return extractAllClaims(token).subject ?: throw InvalidJwtTokenException("Account name not found in token")
    }

    fun extractAccountId(token: String): UUID {
        val claims = extractAllClaims(token)
        return UUID.fromString(claims["accountId"].toString() ?: throw InvalidJwtTokenException("Account ID not found in token"))
    }

    fun extractAccountRoles(token: String): List<AccountRoles> {
        val claims = extractAllClaims(token)
        return (claims["accountRoles"] as List<*>).map { AccountRoles.valueOf(it.toString()) }
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            val claims = Jwts.parser()
              .setSigningKey(signingKey)
              .build()
              .parseClaimsJws(token)
              .body
            return claims
        } catch (ex: Exception) {
            throw InvalidJwtTokenException("Failed to parse JWT token: ${ex.message}")
        }
    }
}