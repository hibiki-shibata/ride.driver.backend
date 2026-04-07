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
import com.ride.driver.backend.shared.auth.domain.JwtTokens
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException

@Service
open class JwtTokenService(
    @Value("\${security.jwt.access-token-validity-ms}")
    private val accessTokenValidityInMilliseconds: String,

    @Value("\${security.jwt.refresh-token-validity-ms}")
    private val refreshTokenValidityInMilliseconds: String,

    @Value("\${security.jwt.secret-string}")
    signingKeyString: String,

    @Value("\${security.jwt.issuer}")
    private val issuer: String,
    
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
) {
    companion object {
        private const val CLAIM_ACCOUNT_ID = "accountId"
        private const val CLAIM_ACCOUNT_NAME = "accountName"
        private const val CLAIM_ACCOUNT_ROLES = "accountRoles"
        private const val CLAIM_SERVICE_TYPE = "serviceType"
        private const val CLAIM_TOKEN_TYPE = "tokenType" 
    }

    fun generateAccessTokenAndRefreshToken(
        jwtTokenClaims: JwtTokenClaims
    ): JwtTokens {
        val accessToken = generateAccessToken(jwtTokenClaims.accessTokenClaim)
        val refreshToken = generateRefreshToken(jwtTokenClaims.refreshTokenClaim)
        return JwtTokens(accessToken = accessToken, refreshToken = refreshToken)
    }

    private fun generateAccessToken(
        accessTokenClaim: AccessTokenClaim
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims =  mapOf(
            CLAIM_ACCOUNT_ID to accessTokenClaim.accountId.toString(),
            CLAIM_ACCOUNT_NAME to accessTokenClaim.accountName,
            CLAIM_ACCOUNT_ROLES to accessTokenClaim.accountRoles.map { it.name },
            CLAIM_SERVICE_TYPE to accessTokenClaim.serviceType.name,
            CLAIM_TOKEN_TYPE to "access"
        )
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(accessTokenClaim.accountId.toString())
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds.toLong()))
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
            .setExpiration(Date(now + refreshTokenValidityInMilliseconds.toLong()))
            .signWith(signingKey)
            .compact()
    }

    private fun validateToken(claims: Claims, expectedServiceType: ServiceType): Unit {
        if (isTokenExpired(claims)) throw InvalidJwtTokenException("JWT token is expired")
        if (!isServiceTypeMatching(claims, expectedServiceType)) throw InvalidJwtTokenException("JWT token service type does not match the expected service type")                
    }

    private fun isTokenExpired(claims: Claims): Boolean {        
        return claims.expiration.before(Date())
    }

    private fun isServiceTypeMatching(claims: Claims, expectedServiceType: ServiceType): Boolean {
        return extractServiceType(claims) == expectedServiceType
    }

    fun extractAccessTokenClaimAndValidate(token: String, expectedServiceType: ServiceType): AccessTokenClaim {
        val claims = extractAllClaims(token)  
        validateToken(claims, expectedServiceType = expectedServiceType)
        return AccessTokenClaim(
            accountId = extractAccountId(claims),
            accountName = extractAccountName(claims),
            accountRoles = extractAccountRoles(claims),
            serviceType = extractServiceType(claims)
        )
    }

    fun extractRefreshTokenClaimAndValidate(token: String, expectedServiceType: ServiceType): RefreshTokenClaim {
        val claims = extractAllClaims(token)
        validateToken(claims, expectedServiceType = expectedServiceType)
        return RefreshTokenClaim(
            accountId = extractAccountId(claims),
            serviceType = extractServiceType(claims)
        )
    }    

    private fun extractAccountName(claims: Claims): String {
        return claims[CLAIM_ACCOUNT_NAME] as? String ?: throw InvalidJwtTokenException("Account name not found in token")
    }

    private fun extractAccountId(claims: Claims): UUID {
        return UUID.fromString(claims.subject.toString() ?: throw InvalidJwtTokenException("Account ID not found in token"))
    }

    private fun extractAccountRoles(claims: Claims): List<AccountRoles> {
        return (claims[CLAIM_ACCOUNT_ROLES] as? List<String>)?.map { AccountRoles.valueOf(it) } ?: throw InvalidJwtTokenException("Account roles not found in token")
    }

    private fun extractServiceType(claims: Claims): ServiceType {
        return ServiceType.valueOf(claims[CLAIM_SERVICE_TYPE].toString()) ?: throw InvalidJwtTokenException("Service type not found in token")
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