package com.ride.driver.backend.merchant.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.Valid
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.merchant.service.MerchantProfileService
import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.dto.MerchantOpenStatusUpdateDTO
import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val merchantProfileService: MerchantProfileService
){
    private val logger: Logger = LoggerFactory.getLogger(MerchantProfileController::class.java)

    @GetMapping("/me")
    fun getMerchantProfile(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        logger.info("event=merchant_profile_request_received merchantId={}", merchantDetails.accountId)
        val merchantProfile: MerchantProfileResDTO = merchantProfileService.getMerchantProfile(merchantDetails)
        return ResponseEntity.ok(merchantProfile)
    }

    @PutMapping("/me")
    fun updateMerchantProfile(
        @RequestBody @Valid req: MerchantProfileReqDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        logger.info("event=merchant_profile_update_request_received merchantId={}", merchantDetails.accountId)
        val updatedProfile: MerchantProfileResDTO = merchantProfileService.updateMerchantProfile(
            merchantDetails = merchantDetails,
            req = req
        )
        return ResponseEntity.ok(updatedProfile)
    }

    @DeleteMapping("/me")
    fun deleteMerchantProfile(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        logger.info("event=merchant_profile_delete_request_received merchantId={}", merchantDetails.accountId)
        merchantProfileService.deleteMerchantProfile(merchantDetails)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/me/status")
    fun updateMerchantOpenStatus(
        @RequestBody @Valid req: MerchantProfileReqDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        logger.info("event=merchant_profile_update_status_request_received merchantId={}", merchantDetails.accountId)
        val updatedProfile: MerchantProfileResDTO = merchantProfileService.updateMerchantOpenStatus(
            merchantDetails = merchantDetails,
            req = req
        )
        return ResponseEntity.ok(updatedProfile)
    }

    @GetMapping("/orders/history")
    fun getMerchantOrderHistory(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<List<MerchantOrderHistoryDTO?>> {
        logger.info("event=merchant_orderHistory_request_received merchantId={}", merchantDetails.accountId)
        val merchantOrderHistory: List<MerchantOrderHistoryDTO?> = merchantProfileService.getMerchantOrderHistory(merchantDetails) 
        return ResponseEntity.ok(merchantOrderHistory)
    } 
 } 