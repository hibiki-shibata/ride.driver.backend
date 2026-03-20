package com.ride.driver.backend.merchant.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.merchant.service.MerchantProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.merchant.dto.MerchantProfileDTO
import com.ride.driver.backend.merchant.dto.MerchantOpenStatusUpdateDTO
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/merchants")
class MerchantProfileController (
    private val merchantProfileService: MerchantProfileService
){
    @GetMapping("/me")
    fun findMerchantProfile(
        @RequestBody @Valid string: String,
        @AuthenticationPrincipal merchantDetails: AccessTokenData
    ): ResponseEntity<MerchantProfileDTO> {
        val merchantProfile = merchantProfileService.getMerchantProfile(merchantDetails.accountID)
        return ResponseEntity.ok(
            MerchantProfileDTO(
                id = merchantProfile?.id,
                name = merchantProfile?.name ?: "",
                phoneNumber = merchantProfile?.phoneNumber ?: "",
                merchantAddress = merchantProfile?.merchantAddress ?: "",
                merchantComments = merchantProfile?.merchantComments,
                merchantStatus = merchantProfile?.merchantStatus.toString(),
                location = merchantProfile?.merchantAddressCoordinate ?: com.ride.driver.backend.shared.model.Coordinate(0.0, 0.0)
            )         
        )
    }

    @PutMapping("/me")
    fun updateMerchantProfile(
        @RequestBody @Valid merchantProfileDTO: MerchantProfileDTO,
        @AuthenticationPrincipal merchantDetails: AccessTokenData
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
        @AuthenticationPrincipal merchantDetails: AccessTokenData
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
}