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
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
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
    fun getConsumerProfile(
        @AuthenticationPrincipal consumerDataInToken: AccessTokenClaim        
    ): ResponseEntity<ConsumerProfileDTO> { 
        logger.info("event=consumer_profile_request_received consumerId={}", consumerDataInToken.accountID)
        val fetchedConsumerProfile: ConsumerProfileDTO = consumerProfileService.getConsumerProfile(consumerDataInToken)
        return ResponseEntity.ok(fetchedConsumerProfile)
    }

    @PutMapping("/me")
    fun updateConsumerProfile(
        @RequestBody @Valid newConsumerProfileData: ConsumerProfileDTO, ////Fix: Differenciate DTO for req and Res?
        @AuthenticationPrincipal consumerDataInToken: AccessTokenClaim
    ): ResponseEntity<ConsumerProfileDTO> {
        logger.info("event=consumer_profile_update_request_received consumerId={}", consumerDataInToken.accountID)
        val updatedConsumerProfile: ConsumerProfileDTO = consumerProfileService.updateConsumerProfile(
            consumerDataInToken = consumerDataInToken,
            newConsumerProfileData = newConsumerProfileData
         )
         return ResponseEntity.ok(updatedConsumerProfile)
     }

    @GetMapping("/order/history")
    fun getConsumerOrderHistory(
        @AuthenticationPrincipal consumerDataInToken: AccessTokenClaim
    ): ResponseEntity<List<ConsumerOrderHistoryDTO>> {      
        logger.info("event=consumer_order_history_request_received consumerId={}", consumerDataInToken.accountID)
        val consumerOrderHistory: List<ConsumerOrderHistoryDTO> = consumerProfileService.getConsumerOrderHistory(consumerDataInToken)
        return ResponseEntity.ok(consumerOrderHistory)
    }         
}
        