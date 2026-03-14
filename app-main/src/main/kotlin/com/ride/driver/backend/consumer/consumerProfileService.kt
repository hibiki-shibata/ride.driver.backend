package com.ride.driver.backend.consumer.services

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate

@Service
class ConsumerProfileService(
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository,
    private val merchantProfileRepository: MerchantProfileRepository
) {
    fun getConsumerProfile(consumerId: UUID): ConsumerProfile {
        val consumer: ConsumerProfile = consumerProfileRepository.findById(consumerId) 
                ?: throw Exception("Consumer not found with ID: $consumerId")
        return consumer
    }

    fun updateConsumerProfile(consumerId: UUID, newEmailAddress: String, newName: String): ConsumerProfile {
       val consumerDetailsInDb: ConsumerProfile = consumerProfileRepository.findById(consumerId) 
            ?: throw Exception("Consumer not found with ID: $consumerId")
        if (newEmailAddress == consumerDetailsInDb.emailAddress)
             throw Exception("Email address is the same as the current one")
        if (consumerProfileRepository.existsByEmailAddress(newEmailAddress)) 
            throw Exception("Email address is already exists by another consumer")
        
        val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(
                consumerDetailsInDb.copy(
                    emailAddress = newEmailAddress,
                    name = newName
                )
        )
        return updatedConsumerProfile
    }

    fun getConsumerOrderHistory(consumerId: UUID): List<Task?> {
        val taskHistories: List<Task?> = taskRepository.findByConsumerProfile_Id(consumerId)  
        return taskHistories ?: emptyList()
    }
}