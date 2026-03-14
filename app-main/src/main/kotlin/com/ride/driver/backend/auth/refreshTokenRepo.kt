package com.ride.driver.backend.auth.repositories
 
import org.springframework.stereotype.Repository
import com.ride.driver.backend.courier.models.CourierProfile

@Repository
class RefreshTokenRepository {
    private val tokens = mutableMapOf<String, CourierProfile>()

    fun findUserDetailsByToken(token: String): CourierProfile? =
        tokens[token]

    fun save(token: String, courierProfile: CourierProfile) {
        tokens[token] = courierProfile
    }
}