package com.ride.driver.backend.services

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import java.util.Date
import java.util.UUID
import java.nio.charset.StandardCharsets
import io.jsonwebtoken.security.Keys
import java.security.Key
import com.ride.driver.backend.exceptions.AuthenticationException


enum class CourierRoles {
    BASE_ROLE,
    ADMIN_ROLE,
    DEVELOPER_ROLE
}

data class AdditionalAccessTokenClaims(
    val courierId: UUID,
    val roles: List<CourierRoles>,
)

data class AccessTokenData(
    val additonalClaims: AdditionalAccessTokenClaims,
    val courierName: String,
)

@Service
open class JwtTokenService(
    private val accessTokenValidityInMilliseconds: Long = 3600000, // 1 hour
    private val refreshTokenValidityInMilliseconds: Long = 86400000, // 24 hours
    private val signingKeyString: String = "this-is-a-very-long-test-secret-key-that-is-at-least-32-bytes!",
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
) {    
    fun generateAccessToken(
        additionalAccessTokenClaims: AdditionalAccessTokenClaims,
        courierName: String,
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims = mapOf("roles" to additionalAccessTokenClaims.roles.map { it.name })
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(courierName)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(
        courierName: String
    ): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(courierName)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + refreshTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    fun extractCourierDetails(token: String): Claims {
        return extractAllClaims(token) ?: throw AuthenticationException("Courier details not found in token")
    }

    fun extractCouriername(token: String): String {
        return extractAllClaims(token).subject ?: throw AuthenticationException("CourierName not found in token")
    }

    fun extractCourierId(token: String): UUID {
        val claims = extractAllClaims(token)
        return claims["courierId"] as? UUID ?: throw AuthenticationException("Courier ID not found in token")
    }

    fun extractRoles(token: String): List<CourierRoles> {
        val claims = extractAllClaims(token)
        return claims["roles"] as? List<CourierRoles> ?: emptyList()
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
            throw AuthenticationException("Failed to extract claims from token: ${ex.message}")
        }
    }
}