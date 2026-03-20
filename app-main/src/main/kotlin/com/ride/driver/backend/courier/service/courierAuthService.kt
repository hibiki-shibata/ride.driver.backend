package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate

import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException

@Service
class CourierAuthService(
    private val courierProfileRepository: CourierProfileRepository,
    private val passwordService: PasswordService
) {
    fun registerNewCourier(
        phoneNumber: String,
        password: String,
        name: String,
        vehicleType: VehicleType
    ): CourierProfile {
        val isCourierExists: Boolean = courierProfileRepository.existsByPhoneNumber(phoneNumber)
        if (isCourierExists) throw AccountConflictException("Courier with phone number ${phoneNumber} already exists")
        val savedNewCourier: CourierProfile = courierProfileRepository.save(
                CourierProfile(
                name = name,
                phoneNumber = phoneNumber,
                passwordHash = passwordService.hashPassword(password),
                vehicleType = vehicleType,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0), // Default location for new couriers
                cpStatus = CourierStatus.ONBOARDING
            )
        )     
        return savedNewCourier
    }

    fun getCourierProfileByPhoneNumberAndValidatePassword(phoneNumber: String, password: String): CourierProfile {
        val savedCourier: CourierProfile = courierProfileRepository.findByPhoneNumber(phoneNumber) ?: 
            throw AccountNotFoundException("Courier with phone number ${phoneNumber} does not exist. Please sign up first.")
        if (!passwordService.isPasswordValid(
            inputPassword = password,
            storedHashedPassword = savedCourier.passwordHash
        )) throw IncorrectPasswordException("Incorrect password for phone number ${phoneNumber}")
        return savedCourier
    }
}