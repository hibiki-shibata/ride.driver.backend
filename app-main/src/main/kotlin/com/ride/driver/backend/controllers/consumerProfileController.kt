package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.ride.driver.backend.repositories.ConsumerProfileRepository
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.services.AccessTokenData
import java.util.UUID

data class ConsumerProfileDTO(
    val id: UUID?,
    val name: String,
    val emailAddress: String,
)

data class ConsumerOrderHistoryDTO(
    val venueName: String,
    val orderDate: String,
    val orderStatus: String,
)


@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController (   
    private val consumerProfileRepository: ConsumerProfileRepository,
    private val taskRepository: TaskRepository
){
    @GetMapping("/consumer/me")
    fun findConsumerProfile(): ResponseEntity<ConsumerProfileDTO> {        
        println("Finding consumer profile...")
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.additonalClaims.accountID
        val consumer: ConsumerProfile = consumerProfileRepository.findById(consumerId) ?: throw Exception("Consumer not found with ID: $consumerId")
        return ResponseEntity.ok(
            ConsumerProfileDTO(
            id = consumer.id,
            name = consumer.cxFirstName + " " + consumer.cxLastName,
            emailAddress = consumer.emailAddress
         )
        )
    }

    @GetMapping("/consumer/order-history")
    fun findConsumerOrderHistory(): ResponseEntity<List<ConsumerOrderHistoryDTO>> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.additonalClaims.accountID
        val tasks: List<Task> = taskRepository.findByConsumerId(consumerId)
        return ResponseEntity.ok(
            tasks.map { task ->
                ConsumerOrderHistoryDTO(
                    venueName = task.venueName,
                    orderDate = task.orderTime.toString(),
                    orderStatus = task.taskStatus.toString()
                )
            }
        )        
    }

    @PostMapping("/consumer/update")
    fun updateConsumerProfile(@RequestBody consumerProfileDTO: ConsumerProfileDTO): ResponseEntity<ConsumerProfileDTO> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData 
            ?: return ResponseEntity.status(401).build()
        val consumerId: UUID = consumerDetails.additonalClaims.accountID        
        val consumerDetailsInDb: ConsumerProfile = consumerProfileRepository.findById(consumerId) 
            ?: throw Exception("Consumer not found with ID: $consumerId")
        if (consumerProfileDTO.emailAddress != consumerDetailsInDb.emailAddress)
             throw Exception("Email address cannot be updated to a different value than the original email address used during registration") 
        if (consumerProfileRepository.existsByEmailAddress(consumerProfileDTO.emailAddress)) 
            throw Exception("Email address ${consumerProfileDTO.emailAddress} is already in use by another consumer")
        
        val updatedConsumerProfile: ConsumerProfile = consumerProfileRepository.save(
                consumerDetailsInDb.copy(emailAddress = consumerProfileDTO.emailAddress)
        )
        return ResponseEntity.ok(
            ConsumerProfileDTO(
                id = updatedConsumerProfile.id,
                name = updatedConsumerProfile.cxFirstName + " " + updatedConsumerProfile.cxLastName,
                emailAddress = updatedConsumerProfile.emailAddress
             )
         )                
    }
}
        