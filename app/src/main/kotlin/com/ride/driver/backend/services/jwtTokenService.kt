package com.ride.driver.backend.service

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import java.util.Date

@Service
open class JwtTokenService(
  @Value("0.13.0") private val secret: String = ""
) {
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }

    fun generateAccessToken( additionalClaims: Map<String, Any>, userName: String ): String {
        val now = Date()
        val expiryDate = Date(now.time + 15 * 60 * 1000) // 15 minutes
        return Jwts.builder()
          .setClaims(additionalClaims)
          .setSubject(userName)
          .setIssuedAt(now)
          .setExpiration(expiryDate)
          .signWith(signingKey)
          .compact()
    }

    fun generateRefreshToken(userName: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 7 * 24 * 60 * 60 * 1000) // 7 days
        return Jwts.builder()
          .setSubject(userName)
          .setIssuedAt(now)
          .setExpiration(expiryDate)
          .signWith(signingKey)
          .compact()
    }

    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractRoles(token: String): List<String> {
        val claims = extractAllClaims(token)
        return claims["roles"] as? List<String> ?: emptyList()
    }

    fun extractUserDetails(token: String): Claims {
        return extractAllClaims(token)
    }

    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token)
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
            throw IllegalArgumentException("Invalid JWT token: ${ex.message}")
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }
}