package com.ride.driver.backend.services

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

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
          .setSigningKey(signingKey)
          .build()
          .parseClaimsJws(token)
          .body
    }
}