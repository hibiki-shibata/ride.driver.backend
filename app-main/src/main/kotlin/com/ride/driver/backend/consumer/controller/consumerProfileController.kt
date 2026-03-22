package com.ride.driver.backend.consumer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.consumer.dto.ConsumerProfileDTO
import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController ( 
    private val consumerProfileService: ConsumerProfileService
){
    private val logger = LoggerFactory.getLogger(ConsumerProfileController::class.java)

    @GetMapping("/me")
    fun findConsumerProfile(
        @AuthenticationPrincipal consumerDetails: AccessTokenData        
    ): ResponseEntity<ConsumerProfileDTO> { 
        logger.info("Received request to get consumer profile for account ID: ${consumerDetails.accountID}")       
        // val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val myConsumerProfile: ConsumerProfile = consumerProfileService.getConsumerProfile(
                consumerId = consumerDetails.accountID
        ) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            ConsumerProfileDTO(
            name = myConsumerProfile.name,
            emailAddress = myConsumerProfile.emailAddress
         )
      )
    }

    @PutMapping("/me")
    fun updateConsumerProfile(
        @RequestBody @Valid  consumerProfileDTO: ConsumerProfileDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenData
    ): ResponseEntity<ConsumerProfileDTO> {
        logger.info("Received request to update consumer profile for account ID: ${consumerDetails.accountID}")        
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

    @GetMapping("/order/history")
    fun findConsumerOrderHistory(
        @AuthenticationPrincipal consumerDetails: AccessTokenData
    ): ResponseEntity<List<ConsumerOrderHistoryDTO?>> {        
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
        