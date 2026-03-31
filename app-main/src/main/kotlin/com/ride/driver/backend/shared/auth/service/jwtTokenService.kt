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
import com.ride.driver.backend.shared.auth.domain.JwtTokenClaims
import com.ride.driver.backend.shared.auth.domain.ServiceType
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

@Service
open class JwtTokenService(
    @Value("\${security.jwt.access-token-validity-ms:36000000}")
    private val accessTokenValidityInMilliseconds: Long,

    @Value("\${security.jwt.refresh-token-validity-ms:86400000}")
    private val refreshTokenValidityInMilliseconds: Long,

    @Value("\${security.jwt.secret-string}")
    signingKeyString: String,

    @Value("\${security.jwt.issuer:ride-backend}")
    private val issuer: String,
    
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
    // private val accessTokenValidityInMilliseconds: Long = 36000000, // 10 hour
    // private val refreshTokenValidityInMilliseconds: Long = 86400000, // 24 hours
    // private val signingKeyString: String = "this-is-a-very-long-test-secret-key-that-is-at-least-32-bytes!",
) {
    companion object {
        private const val CLAIM_ACCOUNT_ID = "accountId"
        private const val CLAIM_ACCOUNT_NAME = "accountName"
        private const val CLAIM_ACCOUNT_ROLES = "accountRoles"
        private const val CLAIM_SERVICE_TYPE = "serviceType"
        private const val CLAIM_TOKEN_TYPE = "tokenType"

        private const val ACCESS_AUDIENCE = "ride-access-api"
        private const val REFRESH_AUDIENCE = "ride-refresh-api"
    }

    fun generateAccessTokenAndRefreshToken(
        jwtTokenClaims: JwtTokenClaims
    ): JwtTokensDTO {
        val accessToken = generateAccessToken(jwtTokenClaims.accessTokenClaim)
        val refreshToken = generateRefreshToken(jwtTokenClaims.refreshTokenClaim)
        return JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun generateAccessToken(
        accessTokenClaim: AccessTokenClaim
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims =  mapOf(
            CLAIM_ACCOUNT_ID to accessTokenClaim.accountId.toString(),
            CLAIM_ACCOUNT_NAME to accessTokenClaim.accountName,
            CLAIM_ACCOUNT_ROLES to accessTokenClaim.accountRoles.map { it.name },
            CLAIM_TOKEN_TYPE to "access"
        )
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(accessTokenClaim.accountId.toString())
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    private fun generateRefreshToken(
        refreshTokenClaim: RefreshTokenClaim
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims =  mapOf(
            CLAIM_ACCOUNT_ID to refreshTokenClaim.accountId.toString(),
            CLAIM_SERVICE_TYPE to refreshTokenClaim.serviceType.name,
            CLAIM_TOKEN_TYPE to "refresh"
        )
        return Jwts.builder()
            .setClaims(additionalClaims)
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
            accountId = UUID.fromString(claims.subject ?: throw InvalidJwtTokenException("Account ID not found in token")),
            accountName = claims["accountName"] as String ?: throw InvalidJwtTokenException("Account name not found in token"),
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