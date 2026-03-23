package com.ride.driver.backend.consumer.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.util.UUID
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import com.ride.driver.backend.consumer.mapper.toConsumerProfileResDTO
import com.ride.driver.backend.consumer.mapper.toConsumerOrderHistoryDto
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.AccountSaveFailedException

@Service
class ConsumerProfileService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerProfileService::class.java)

    fun getConsumerProfile(consumerDataInToken: AccessTokenClaim): ConsumerProfileResDTO {
        val savedConsumerProfile: ConsumerProfile = getConsumerProfileById(consumerDataInToken.accountID)
        logger.info("event=consumer_profile_fetched consumerId={}", consumerDataInToken.accountID)
        return savedConsumerProfile.toConsumerProfileResDTO() 
    }
    
    @Transactional
    fun updateConsumerProfile(
        consumerDataInToken: AccessTokenClaim, 
        newConsumerProfileData: ConsumerProfileReqDTO
    ): ConsumerProfileResDTO {    
       val savedConsumerProfile: ConsumerProfile = getConsumerProfileById(consumerDataInToken.accountID)
       val emailChanged: Boolean = newConsumerProfileData.emailAddress != savedConsumerProfile.emailAddress
       if (consumerProfileRepository.existsByEmailAddress(newConsumerProfileData.emailAddress) && emailChanged)
            throw AccountConflictException("Consumer with request email address already exists")
       
       val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(
        savedConsumerProfile.apply {
            name = newConsumerProfileData.name
            emailAddress = newConsumerProfileData.emailAddress
            consumerAddress = newConsumerProfileData.consumerAddress
            consumerAddressCoordinate = newConsumerProfileData.consumerAddressCoordinate
        }
       )
       logger.info("event=consumer_profile_update_completed consumerId={}", consumerDataInToken.accountID)
       return updatedConsumerProfile.toConsumerProfileResDTO()
    }

    private fun getConsumerProfileById(consumerId: UUID): ConsumerProfile {
        return consumerProfileRepository.findById(consumerId).orElseThrow { 
            AccountNotFoundException("Consumer not found with ID: $consumerId") 
        }
    }

    fun getConsumerOrderHistory(consumerDataInToken: AccessTokenClaim): List<ConsumerOrderHistoryDTO> {
        val taskHistories: List<Task> = taskRepository.findByConsumerProfile_Id(consumerDataInToken.accountID)
        logger.info(
            "event=consumer_order_history_fetched consumerId={} totalOrders={}", consumerDataInToken.accountID, taskHistories.size
        )
        return taskHistories.map { it.toConsumerOrderHistoryDto()}
    }
}