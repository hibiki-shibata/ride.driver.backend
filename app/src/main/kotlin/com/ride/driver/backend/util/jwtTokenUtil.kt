package com.ride.driver.backend.util

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Value
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Claims
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import java.util.Date

@Service
open class JwtTokenUtil(
  @Value("0.13.0") private val secret: String = ""
) {
    private val signingKey: SecretKeySpec
        get() {
            val keyBytes: ByteArray = Base64.getDecoder().decode(secret)
            return SecretKeySpec(keyBytes, 0, keyBytes.size, "HmacSHA256")
        }

    fun generateToken(subject: String, expiration: Date, additionalClaims: Map<String, Any> = emptyMap()): String {
        return Jwts.builder()
          .setClaims(additionalClaims)
          .setSubject(subject)
          .setIssuedAt(Date(System.currentTimeMillis()))
          .setExpiration(expiration)
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
        val isTokenExpired = extractAllClaims(token).expiration.before(Date())
        return !isTokenExpired
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