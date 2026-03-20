package com.ride.driver.backend.consumer.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException


@Service
class ConsumerAuthService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val passwordService: PasswordService
) {
    fun registerNewConsumer(
        name: String,
        emailAddress: String,
        password: String,
        consumerAddress: String,
        consumerAddressCoordinate: Coordinate
    ): ConsumerProfile {
        val isConsumerExists: Boolean = consumerProfileRepository.existsByEmailAddress(emailAddress)
        if (isConsumerExists) throw AccountConflictException("Consumer with email address ${emailAddress} already exists")
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = name,
                emailAddress = emailAddress,
                consumerAddress = consumerAddress,
                consumerAddressCoordinate = consumerAddressCoordinate,
                passwordHash = passwordService.hashPassword(password)
        ))
        return savedConsumer
    }

    fun getConsumerProfileByEmailAdderessAndValidatePassword(
        emailAddress: String,
        password: String
    ): ConsumerProfile {
        val savedConsumer: ConsumerProfile = consumerProfileRepository.findByEmailAddress(emailAddress) ?: 
            throw AccountNotFoundException("Consumer with email address ${emailAddress} does not exist. Please sign up first.")
        if (!passwordService.isPasswordValid(
            inputPassword = password,
            storedHashedPassword = savedConsumer.passwordHash
        )) throw IncorrectPasswordException("Incorrect password for email address ${emailAddress}")
        return savedConsumer
    }
}