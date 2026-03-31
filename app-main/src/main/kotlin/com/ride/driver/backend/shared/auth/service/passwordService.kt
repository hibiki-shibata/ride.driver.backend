package com.ride.driver.backend.shared.auth.service

import org.springframework.stereotype.Service
import org.mindrot.jbcrypt.BCrypt

// I will imlement decent password service later!!
@Service
class PasswordService {
    fun hashPassword(inputPassword: String): String {
        // return inputPassword.hashCode().toString()
        return BCrypt.hashpw(inputPassword, BCrypt.gensalt())
    }

    fun isPasswordValid(inputPassword: String, storedHashedPassword: String): Boolean {
        return inputPassword.hashCode().toString() == storedHashedPassword
    }
}