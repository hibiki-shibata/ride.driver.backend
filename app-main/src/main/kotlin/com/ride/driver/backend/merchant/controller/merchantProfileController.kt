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
import com.ride.driver.backend.merchant.dto.MerchantProfileDTO
import com.ride.driver.backend.merchant.dto.MerchantOpenStatusUpdateDTO
import com.ride.driver.backend.merchant.dto.MerchantOrderHistoryDTO
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val merchantProfileService: MerchantProfileService
){
    @GetMapping("/me")
    fun findMerchantProfile(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileDTO> {
        val merchantProfile = merchantProfileService.getMerchantProfile(merchantDetails.accountID) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            MerchantProfileDTO(
                id = merchantProfile?.id,
                name = merchantProfile?.name ?: "",
                phoneNumber = merchantProfile?.phoneNumber ?: "",
                merchantAddress = merchantProfile?.merchantAddress ?: "",
                merchantComments = merchantProfile?.merchantComments,
                merchantStatus = merchantProfile?.merchantStatus?.toString() ?: "Unknown Status",
                location = merchantProfile?.merchantAddressCoordinate ?: Coordinate(0.0, 0.0)
            )         
        )
    }

    @PutMapping("/me")
    fun updateMerchantProfile(
        @RequestBody @Valid merchantProfileDTO: MerchantProfileDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileDTO> {
        val updatedProfile = merchantProfileService.updateMerchantProfile(
            merchantId = merchantDetails.accountID,
            newName = merchantProfileDTO.name,
            newPhoneNumber = merchantProfileDTO.phoneNumber,
            newMerchantAddress = merchantProfileDTO.merchantAddress,
            newMerchantComments = merchantProfileDTO.merchantComments,
            newMerchantAddressCoordinate = merchantProfileDTO.location
        )
        return ResponseEntity.ok(
            MerchantProfileDTO(
                id = updatedProfile.id,
                name = updatedProfile.name,
                phoneNumber = updatedProfile.phoneNumber,
                merchantAddress = updatedProfile.merchantAddress,
                merchantComments = updatedProfile.merchantComments,
                merchantStatus = updatedProfile.merchantStatus.toString(),
                location = updatedProfile.merchantAddressCoordinate
            )         
        )
    }

    @PutMapping("/me/status")
    fun updateMerchantOpenStatus(
        @RequestBody @Valid isMerchantOpen: MerchantOpenStatusUpdateDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<MerchantProfileDTO> {
        val updatedProfile = merchantProfileService.updateMerchantOpenStatus(
            merchantId = merchantDetails.accountID,
            isOpen = isMerchantOpen.isOpen
        )
        return ResponseEntity.ok(
            MerchantProfileDTO(
                id = updatedProfile.id,
                name = updatedProfile.name,
                phoneNumber = updatedProfile.phoneNumber,
                merchantAddress = updatedProfile.merchantAddress,
                merchantComments = updatedProfile.merchantComments,
                merchantStatus = updatedProfile.merchantStatus.toString(),
                location = updatedProfile.merchantAddressCoordinate
            )         
        )
    }

    @GetMapping("/orders/history")
    fun findMerchantOrderHistory(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<List<MerchantOrderHistoryDTO>> {
        val merchantOrderHistory: List<Task> = merchantProfileService.getMerchantOrderHistory(
                merchantId = merchantDetails.accountID
        ) 
        return ResponseEntity.ok(
            merchantOrderHistory.map { task ->
                MerchantOrderHistoryDTO(
                    orderId = task.id, 
                    consumerName = task.consumerProfile?.name ?: "Unknown Consumer",
                    orderTime = task.orderTime?.toString() ?: "Unknown Date",
                    orderStatus = task.taskStatus?.toString() ?: "Unknown Status"
                )
            }          
        )
    } 
 } 