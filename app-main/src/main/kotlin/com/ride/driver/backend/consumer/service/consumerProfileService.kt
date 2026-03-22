package com.ride.driver.backend.consumer.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.dto.ConsumerProfileDTO
import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import com.ride.driver.backend.consumer.mapper.toConsumerProfileDTO
import com.ride.driver.backend.consumer.mapper.toConsumerOrderHistoryDto
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.exception.AccountConflictException
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.AccountSaveFailedException


@Service
class ConsumerProfileService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository
) {
    fun getConsumerProfile(consumerDataInToken: AccessTokenData): ConsumerProfileDTO {
        val savedConsumer: ConsumerProfile = consumerProfileRepository.findById(consumerDataInToken.accountID).orElseThrow { 
            AccountNotFoundException("Consumer not found with ID: ${consumerDataInToken.accountID}") 
        }
        return savedConsumer.toConsumerProfileDTO() 
    }
    
    //Fix: Consider differentiating DTO for update req and get res to avoid confusion and potential issues with validation
    @Transactional
    fun updateConsumerProfile(
        consumerDataInToken: AccessTokenData, 
        newConsumerProfileData: ConsumerProfileDTO
    ): ConsumerProfileDTO {    
       val savedConsumerProfile: ConsumerProfile = consumerProfileRepository.findById(consumerDataInToken.accountID).orElseThrow { 
            AccountNotFoundException("Consumer not found with ID: ${consumerDataInToken.accountID}") 
        }
       val emailChanged: Boolean = newConsumerProfileData.emailAddress != savedConsumerProfile.emailAddress
       if (consumerProfileRepository.existsByEmailAddress(newConsumerProfileData.emailAddress) && emailChanged)
            throw AccountConflictException("Consumer with email address ${newConsumerProfileData.emailAddress} already exists")
       val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(
            savedConsumerProfile.copy(
                name = newConsumerProfileData.name,
                emailAddress = newConsumerProfileData.emailAddress
            )
       )
       return updatedConsumerProfile.toConsumerProfileDTO()
    }

    fun getConsumerOrderHistory(consumerDataInToken: AccessTokenData): List<ConsumerOrderHistoryDTO> {
        val taskHistories: List<Task> = taskRepository.findByConsumerProfile_Id(consumerDataInToken.accountID)
        return taskHistories.map { it.toConsumerOrderHistoryDto()}
    }
}