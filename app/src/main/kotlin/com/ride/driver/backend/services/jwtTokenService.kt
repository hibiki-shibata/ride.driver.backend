package com.ride.driver.backend.services

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import java.util.Date
import java.nio.charset.StandardCharsets
import io.jsonwebtoken.security.Keys
import java.security.Key
import com.ride.driver.backend.exceptions.AuthenticationException

enum class CourierRoles {
    BASE_ROLE,
    ADMIN_ROLE,
    DEVELOPER_ROLE
}

data class AdditionalAccessTokenClaims(val roles: List<CourierRoles>)

@Service
open class JwtTokenService(
    private val accessTokenValidityInMilliseconds: Long = 3600000, // 1 hour
    private val refreshTokenValidityInMilliseconds: Long = 86400000, // 24 hours
    private val signingKeyString: String = "this-is-a-very-long-test-secret-key-that-is-at-least-32-bytes!",
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
) {    
    fun generateAccessToken(
        additionalAccessTokenClaims: AdditionalAccessTokenClaims,
        userName: String,
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims = mapOf("roles" to additionalAccessTokenClaims.roles.map { it.name })
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(userName)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(
        userName: String
    ): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setSubject(userName)
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

    fun extractUserDetails(token: String): Claims {
        return extractAllClaims(token) ?: throw AuthenticationException("User details not found in token")
    }

    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject ?: throw AuthenticationException("Username not found in token")
    }

    fun extractRoles(token: String): List<String> {
        val claims = extractAllClaims(token)
        return claims["roles"] as? List<String> ?: emptyList()
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
            throw AuthenticationException("Invalid JWT Token: ${ex.message}")
        }
    }
}