package com.ride.driver.backend.merchant.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.merchant.service.MerchantProfileService
import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.dto.MerchantOpenStatusUpdateDTO
import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val merchantProfileService: MerchantProfileService
){
    @GetMapping("/me")
    fun getMerchantProfile(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        val merchantProfile: MerchantProfileResDTO = merchantProfileService.getMerchantProfile(merchantDetails)
        return ResponseEntity.ok(merchantProfile)
    }

    @PutMapping("/me")
    fun updateMerchantProfile(
        @RequestBody @Valid req: MerchantProfileReqDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        val updatedProfile: MerchantProfileResDTO = merchantProfileService.updateMerchantProfile(
            merchantDetails = merchantDetails,
            req = req
        )
        return ResponseEntity.ok(updatedProfile)
    }

    @PutMapping("/me/status")
    fun updateMerchantOpenStatus(
        @RequestBody @Valid req: MerchantProfileReqDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileResDTO> {
        val updatedProfile: MerchantProfileResDTO = merchantProfileService.updateMerchantOpenStatus(
            merchantDetails = merchantDetails,
            req = req
        )
        return ResponseEntity.ok(updatedProfile)
    }

    @GetMapping("/orders/history")
    fun getMerchantOrderHistory(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<List<MerchantOrderHistoryDTO>> {
        val merchantOrderHistory: List<MerchantOrderHistoryDTO> = merchantProfileService.getMerchantOrderHistory(merchantDetails) 
        return ResponseEntity.ok(merchantOrderHistory)
    } 
 } 