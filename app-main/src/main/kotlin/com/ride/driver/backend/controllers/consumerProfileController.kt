package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.ride.driver.backend.repositories.ConsumerProfileRepository
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import com.ride.driver.backend.services.AccessTokenData
import java.util.UUID

data class ConsumerProfileDTO(
    val id: UUID?,
    val name: String,
    val emailAddress: String,
)

@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController (   
    private val consumerProfileRepository: ConsumerProfileRepository,
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
}