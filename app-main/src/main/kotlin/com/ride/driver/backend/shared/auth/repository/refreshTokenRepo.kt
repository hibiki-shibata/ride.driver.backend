package com.ride.driver.backend.shared.auth.repository
 
import org.springframework.stereotype.Repository
import com.ride.driver.backend.courier.model.CourierProfile

@Repository
class RefreshTokenRepository {
    private val tokens = mutableMapOf<String, CourierProfile>()

    fun findUserDetailsByToken(token: String): CourierProfile? =
        tokens[token]

    fun save(token: String, courierProfile: CourierProfile) {
        tokens[token] = courierProfile
    }
}