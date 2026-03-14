package com.ride.driver.backend.consumer.controllers

import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import com.ride.driver.backend.consumer.services.ConsumerProfileService
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.auth.domain.AccessTokenData


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
    private val consumerProfileService: ConsumerProfileService
){
    @GetMapping("/consumer/me")
    fun findConsumerProfile(): ResponseEntity<ConsumerProfileDTO> {        
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val myConsumerProfile: ConsumerProfile = consumerProfileService.getConsumerProfile(
                consumerId = consumerDetails.accountID
        )
        return ResponseEntity.ok(
            ConsumerProfileDTO(
            name = myConsumerProfile.name,
            emailAddress = myConsumerProfile.emailAddress
         )
      )
    }

    @PostMapping("/consumer/update")
    fun updateConsumerProfile(@RequestBody consumerProfileDTO: ConsumerProfileDTO): ResponseEntity<ConsumerProfileDTO> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()        
        val updatedConsumerProfile: ConsumerProfile = consumerProfileService.updateConsumerProfile(
                consumerId = consumerDetails.accountID,
                newEmailAddress = consumerProfileDTO.emailAddress,
                newName = consumerProfileDTO.name
        )
        return ResponseEntity.ok(
            ConsumerProfileDTO(
                name = updatedConsumerProfile.name,
                emailAddress = updatedConsumerProfile.emailAddress
             )
         )

    @GetMapping("/consumer/order/history")
    fun findConsumerOrderHistory(): ResponseEntity<List<ConsumerOrderHistoryDTO?>> {
        val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val consumerOrderHistory: List<Task?> = consumerProfileService.getConsumerOrderHistory(
                consumerId = consumerDetails.accountID
        )
        return ResponseEntity.ok(
            consumerOrderHistory.map { task ->
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
        