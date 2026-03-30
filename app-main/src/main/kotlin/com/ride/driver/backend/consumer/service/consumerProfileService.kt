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

@Service
class ConsumerProfileService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerProfileService::class.java)

    fun getConsumerProfile(consumerDetails: AccessTokenClaim): ConsumerProfileResDTO {
        val savedConsumerProfile: ConsumerProfile = getConsumerProfileById(consumerDetails.accountId)
        logger.info("event=consumer_profile_fetched consumerId={}", consumerDetails.accountId)
        return savedConsumerProfile.toConsumerProfileResDTO() 
    }
    
    @Transactional
    fun updateConsumerProfile(
        consumerDetails: AccessTokenClaim, 
        newConsumerProfileData: ConsumerProfileReqDTO
    ): ConsumerProfileResDTO {    
       val savedConsumerProfile: ConsumerProfile = getConsumerProfileById(consumerDetails.accountId)
       val emailChanged: Boolean = newConsumerProfileData.emailAddress != savedConsumerProfile.emailAddress
       if (consumerProfileRepository.existsByEmailAddress(newConsumerProfileData.emailAddress) && emailChanged)
            throw AccountConflictException("Consumer with request email address already exists")
        // Apply changed data to fetched consumer profile and save to DB
       savedConsumerProfile.apply {
            name = newConsumerProfileData.name
            emailAddress = newConsumerProfileData.emailAddress
            consumerAddress = newConsumerProfileData.consumerAddress
            consumerAddressCoordinate = newConsumerProfileData.consumerAddressCoordinate
       }
       val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(savedConsumerProfile)
       logger.info("event=consumer_profile_update_completed consumerId={}", consumerDetails.accountId)
       return updatedConsumerProfile.toConsumerProfileResDTO()
    }

    private fun getConsumerProfileById(consumerId: UUID): ConsumerProfile {
        return consumerProfileRepository.findById(consumerId).orElseThrow { 
            AccountNotFoundException("Consumer not found with ID: $consumerId") 
        }
    }

    fun getConsumerOrderHistory(consumerDetails: AccessTokenClaim): List<ConsumerOrderHistoryDTO?> {
        val taskHistories: List<Task> = taskRepository.findByConsumerProfile_Id(consumerDetails.accountId) ?: emptyList()
        logger.info(
            "event=consumer_order_history_fetched consumerId={} totalOrders={}", consumerDetails.accountId, taskHistories.size
        )
        return taskHistories.map { it?.toConsumerOrderHistoryDto()}
    }
}