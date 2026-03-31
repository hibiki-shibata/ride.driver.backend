package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import com.ride.driver.backend.shared.exception.PasswordEncodingException

@Service
class PasswordService(
    private val encoder: PasswordEncoder
) {
    fun hashPassword(inputPassword: String): String {
        return encoder.encode(inputPassword) ?: throw PasswordEncodingException("Password hashing failed")
    }

    fun isPasswordValid(inputPassword: String, storedHashedPassword: String): Boolean {
        return encoder.matches(inputPassword, storedHashedPassword) ?: throw PasswordEncodingException("Password validation failed")
    }
}