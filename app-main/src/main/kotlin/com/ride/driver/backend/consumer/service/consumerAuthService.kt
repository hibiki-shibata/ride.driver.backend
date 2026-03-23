package com.ride.driver.backend.consumer.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.consumer.mapper.toAccessTokenClaim
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import com.ride.driver.backend.shared.auth.dto.JwtTokensDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
class ConsumerAuthService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val passwordService: PasswordService,
    private val jwtTokenService: JwtTokenService
) {

    private val logger: Logger = LoggerFactory.getLogger(ConsumerAuthService::class.java)

    fun signupConsumer(req: ConsumerSignupDTO): JwtTokensDTO {
        if (consumerProfileRepository.existsByEmailAddress(req.emailAddress))
             throw AccountConflictException("Consumer with request email address already exists")
        
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
                ConsumerProfile(
                    name = req.name,
                    emailAddress = req.emailAddress,
                    consumerAddress = req.consumerAddress,
                    consumerAddressCoordinate = req.consumerAddressCoordinate,
                    passwordHash = passwordService.hashPassword(req.password)
            ))   
        
        logger.info("event=consumer_signup_successful consumerId={}", savedConsumer.id)
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedConsumer.toAccessTokenClaim())
    }

    fun loginConsumer(req: ConsumerLoginDTO): JwtTokensDTO {
        val savedConsumer: ConsumerProfile = consumerProfileRepository.findByEmailAddress(req.emailAddress)
            ?: throw AccountNotFoundException("Consumer not found with request email address")
        val isPasswordValid: Boolean = passwordService.isPasswordValid(
            inputPassword = req.password,
            storedHashedPassword = savedConsumer.passwordHash
        )
        if (!isPasswordValid) throw IncorrectPasswordException("Incorrect password for login consumer")
        logger.info("event=consumer_login_successful consumerId={}", savedConsumer.id)
        return jwtTokenService.generateAccessTokenAndRefreshToken(savedConsumer.toAccessTokenClaim())
    }
}