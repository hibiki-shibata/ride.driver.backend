package com.ride.driver.backend.consumer.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.dto.ConsumerSignupDTO
import com.ride.driver.backend.consumer.dto.ConsumerLoginDTO
import com.ride.driver.backend.shared.auth.service.PasswordService
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.IncorrectPasswordException
import com.ride.driver.backend.shared.auth.service.JwtTokenService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
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

    fun signupNewConsumer(req: ConsumerSignupDTO): JwtTokensDTO {
        logger.info("Attempting to register new consumer")
        if (consumerProfileRepository.existsByEmailAddress(req.emailAddress)){
             throw AccountConflictException("Consumer with email address ${req.emailAddress} already exists.")
        }
        val savedConsumer: ConsumerProfile = consumerProfileRepository.save(
                ConsumerProfile(
                    name = req.name,
                    emailAddress = req.emailAddress,
                    consumerAddress = req.consumerAddress,
                    consumerAddressCoordinate = req.consumerAddressCoordinate,
                    passwordHash = passwordService.hashPassword(req.password)
            ))        

        return issueJwtTokensForConsumer(savedConsumer)
    }

    fun loginConsumer(req: ConsumerLoginDTO): JwtTokensDTO {
        logger.info("Attempting to authenticate consumer")
        val savedConsumer: ConsumerProfile = consumerProfileRepository.findByEmailAddress(req.emailAddress)
            ?: throw AccountNotFoundException("Consumer with email address ${req.emailAddress} not found.")
        val isPasswordValid: Boolean = passwordService.isPasswordValid(
            inputPassword = req.password,
            storedHashedPassword = savedConsumer.passwordHash
        )
        if (!isPasswordValid) throw IncorrectPasswordException("Incorrect password for email address ${req.emailAddress}")
        return issueJwtTokensForConsumer(savedConsumer)
    }

    private fun issueJwtTokensForConsumer(
        consumerProfile: ConsumerProfile
    ): JwtTokensDTO {
        return jwtTokenService.generateAccessTokenAndRefreshToken(
            AccessTokenData(
                accountID = consumerProfile.id ?: throw AccountNotFoundException("Consumer ID is null"),
                accountName = consumerProfile.name,
                accountRoles = listOf(AccountRoles.BASE_ROLE)
            )
        )
    }
}