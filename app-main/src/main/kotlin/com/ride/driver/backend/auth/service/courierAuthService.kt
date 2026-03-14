package com.ride.driver.backend.auth.services

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.models.VehicleType
import com.ride.driver.backend.courier.repositories.CourierProfileRepository


@Service
class CourierAuthService(
    private val courierProfileRepository: CourierProfileRepository
) {
    fun registerNewCourier(
        phoneNumber: String,
        password: String,
        name: String,
        vehicleType: VehicleType
    ): CourierProfile {
        val isCourierExists: Boolean = courierProfileRepository.existsByPhoneNumber(phoneNumber)
        if (isCourierExists) throw BadRequestException("Courier with phone number ${phoneNumber} already exists")          
        val savedNewCourier: CourierProfile = courierProfileRepository.save(
                CourierProfile(
                name = name,
                phoneNumber = phoneNumber,
                passwordHash = password.hashCode().toString(), // Simple hash for demonstration. Use a proper hashing algorithm in production.
                vehicleType = vehicleType,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0), // Default location for new couriers
                cpStatus = CourierStatus.ONBOARDING
            )
        )     
        return savedNewCourier
    }

    fun getCourierProfileByPhoneNumber(phoneNumber: String): CourierProfile {
            val savedCourier: CourierProfile = courierProfileRepository.findByPhoneNumber(phoneNumber) ?: 
                throw BadRequestException("Courier with phone number ${phoneNumber} does not exist. Please sign up first.")
        return savedCourier
    }
}