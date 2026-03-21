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
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.exception.InvalidJwtTokenException
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO

@Service
open class JwtTokenService(
    private val accessTokenValidityInMilliseconds: Long = 36000000, // 10 hour
    private val refreshTokenValidityInMilliseconds: Long = 86400000, // 24 hours
    private val signingKeyString: String = "this-is-a-very-long-test-secret-key-that-is-at-least-32-bytes!",
    private val signingKey: Key = Keys.hmacShaKeyFor(signingKeyString.toByteArray(StandardCharsets.UTF_8)),
) {    
    fun generateAccessTokenAndRefreshToken(
        accountTokenData: AccessTokenData
    ): JwtTokensDTO {
        val accessToken = generateAccessToken(accountTokenData)
        val refreshToken = generateRefreshToken(accountTokenData.accountID)
        return JwtTokensDTO(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun generateAccessToken(
        accountTokenData: AccessTokenData
    ): String {
        val now = System.currentTimeMillis()
        val additionalClaims =  mapOf(
            "accountID" to accountTokenData.accountID.toString(),
            "accountRoles" to accountTokenData.accountRoles.map { it.name }
        )
        return Jwts.builder()
            .setClaims(additionalClaims)
            .setSubject(accountTokenData.accountName)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + accessTokenValidityInMilliseconds))
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(
        accountID: UUID
    ): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .setClaims(mapOf("accountID" to accountID.toString()))
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

    fun extractAccessTokenData(token: String): AccessTokenData {
        val claims = extractAllClaims(token)
        return AccessTokenData(
            accountID = UUID.fromString(claims["accountID"].toString() ?: throw InvalidJwtTokenException("Account ID not found in token")),
            accountName = claims.subject ?: throw InvalidJwtTokenException("Account name not found in token"),
            accountRoles = (claims["accountRoles"] as List<*>).map { AccountRoles.valueOf(it.toString()) }
        )
    }

    fun extractAccountName(token: String): String {
        return extractAllClaims(token).subject ?: throw InvalidJwtTokenException("Account name not found in token")
    }

    fun extractAccountId(token: String): UUID {
        val claims = extractAllClaims(token)
        return UUID.fromString(claims["accountID"].toString() ?: throw InvalidJwtTokenException("Account ID not found in token"))
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