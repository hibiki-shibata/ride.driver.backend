package com.ride.driver.backend.consumer.controller

import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate

data class ConsumerProfileDTO(
    val name: String,
    val emailAddress: String,
)

data class ConsumerOrderHistoryDTO(
    val merchantName: String,
    val orderTime: String,
    val orderStatus: String,
)

@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController (   
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository,
    private val merchantProfileRepository: MerchantProfileRepository
){
    @GetMapping("/consumer/me")
    fun findConsumerProfile(): ResponseEntity<ConsumerProfileDTO> {        
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.accountID
        val consumer: ConsumerProfile = consumerProfileRepository.findById(consumerId) ?: throw Exception("Consumer not found with ID: $consumerId")
        return ResponseEntity.ok(
            ConsumerProfileDTO(
            name = consumer.name,
            emailAddress = consumer.emailAddress
         )
        )
    }

    @PostMapping("/consumer/update")
    fun updateConsumerProfile(@RequestBody consumerProfileDTO: ConsumerProfileDTO): ResponseEntity<ConsumerProfileDTO> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData 
            ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.accountID        
        val consumerDetailsInDb: ConsumerProfile = consumerProfileRepository.findById(consumerId) 
            ?: throw Exception("Consumer not found with ID: $consumerId")
        if (consumerProfileDTO.emailAddress == consumerDetailsInDb.emailAddress) throw Exception("The new email address is the same as the current email address")
        if (consumerProfileRepository.existsByEmailAddress(consumerProfileDTO.emailAddress)) 
            throw Exception("Email address ${consumerProfileDTO.emailAddress} is already in use by another consumer")
        
        val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(
                consumerDetailsInDb.copy(
                    emailAddress = consumerProfileDTO.emailAddress,
                    name = consumerProfileDTO.name
                )
        )
        return ResponseEntity.ok(
            ConsumerProfileDTO(
                name = updatedConsumerProfile.name,
                emailAddress = updatedConsumerProfile.emailAddress
             )
         )

    @GetMapping("/consumer/order/history")
    fun findConsumerOrderHistory(): ResponseEntity<List<ConsumerOrderHistoryDTO>> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.accountID
        val tasks: List<Task?> = taskRepository.findByConsumerProfile_Id(consumerId)        
        return ResponseEntity.ok(
            tasks.map { task ->
                ConsumerOrderHistoryDTO(
                    merchantName = task?.merchantProfile?.name ?: "Unknown Merchant",
                    orderTime = task?.orderTime?.toString() ?: "Unknown Date",
                    orderStatus = task?.taskStatus?.toString() ?: "Unknown Status"
                )
            }
        )        
    }         
  }
}
        