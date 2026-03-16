package com.ride.driver.backend.consumer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.format.annotation.DateTimeFormat


data class ConsumerProfileDTO(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Email
    val emailAddress: String,
)

data class ConsumerOrderHistoryDTO(
    @field:NotBlank
    val merchantName: String,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:NotBlank 
    val orderTime: String,

    @field:NotBlank
    val orderStatus: String,
)

@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController ( 
    private val consumerProfileService: ConsumerProfileService
){
    @GetMapping("/me")
    fun findConsumerProfile(
        @AuthenticationPrincipal consumerDetails: AccessTokenData
    ): ResponseEntity<ConsumerProfileDTO> {        
        // val consumerDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
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

    @PutMapping("/me")
    fun updateConsumerProfile(
        @RequestBody consumerProfileDTO: ConsumerProfileDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenData
    ): ResponseEntity<ConsumerProfileDTO> {
        
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
        