package com.ride.driver.backend.auth.services

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.shared.exceptions.BadRequestException
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository


@Service
class ConsumerAuthService(
    private val consumerProfileRepository: ConsumerProfileRepository
) {
    fun registerNewConsumer(
        name: String,
        emailAddress: String,
        password: String,
        homeAddress: String,
        homeAddressCoordinate: Coordinate
    ): ConsumerProfile {
        val isConsumerExists: Boolean = consumerProfileRepository.existsByEmailAddress(emailAddress)
        if (isConsumerExists) throw BadRequestException("Consumer with email address ${emailAddress} already exists")
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = name,
                emailAddress = emailAddress,
                homeAddress = homeAddress,
                homeAddressCoordinate = homeAddressCoordinate,
                passwordHash = password.hashCode().toString() // Simple hash for demonstration. Use a proper hashing algorithm in production.
        ))
        return savedConsumer
    }

    fun getConsumerProfileByEmailAddress(emailAddress: String): ConsumerProfile {
        val savedConsumerProfile: ConsumerProfile = consumerProfileRepository.findByEmailAddress(emailAddress) ?: 
            throw BadRequestException("Consumer with email address ${emailAddress} does not exist. Please sign up first.")
        return savedConsumerProfile    
    }
}