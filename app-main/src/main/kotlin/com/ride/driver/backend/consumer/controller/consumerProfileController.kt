package com.ride.driver.backend.consumer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.Valid
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.dto.ConsumerOrderHistoryDTO

@RestController
@RequestMapping("api/v1/consumers")
class ConsumerProfileController ( 
    private val consumerProfileService: ConsumerProfileService
){
    private val logger = LoggerFactory.getLogger(ConsumerProfileController::class.java)

    @GetMapping("/me")
    fun getConsumerProfile(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim        
    ): ResponseEntity<ConsumerProfileResDTO> { 
        logger.info("event=consumer_profile_request_received consumerId={}", consumerDetails.accountId)
        val fetchedConsumerProfile: ConsumerProfileResDTO = consumerProfileService.getConsumerProfile(consumerDetails)
        return ResponseEntity.ok(fetchedConsumerProfile)
    }

    @PutMapping("/me")
    fun updateConsumerProfile(
        @RequestBody @Valid newConsumerProfileData: ConsumerProfileReqDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim
    ): ResponseEntity<ConsumerProfileResDTO> {
        logger.info("event=consumer_profile_update_request_received consumerId={}", consumerDetails.accountId)
        val updatedConsumerProfile: ConsumerProfileResDTO = consumerProfileService.updateConsumerProfile(
            consumerDetails = consumerDetails,
            newConsumerProfileData = newConsumerProfileData
         )
         return ResponseEntity.ok(updatedConsumerProfile)
    }

    @DeleteMapping("/me")
    fun deleteConsumerProfile(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        logger.info("event=consumer_profile_delete_request_received consumerId={}", consumerDetails.accountId)
        consumerProfileService.deleteConsumerProfile(consumerDetails)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/order/history")
    fun getConsumerOrderHistory(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim
    ): ResponseEntity<List<ConsumerOrderHistoryDTO?>> {      
        logger.info("event=consumer_order_history_request_received consumerId={}", consumerDetails.accountId)
        val consumerOrderHistory: List<ConsumerOrderHistoryDTO?> = consumerProfileService.getConsumerOrderHistory(consumerDetails)
        return ResponseEntity.ok(consumerOrderHistory)
    }         
}
        